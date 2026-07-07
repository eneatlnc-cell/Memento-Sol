#!/usr/bin/env python3
"""剥离 libllama.so 的 DT_NEEDED + VERNEED 中的 libcdsprpc.so 和 libOpenCL.so。

在 npm install 下载 llama.rn 的预编译 .so 后运行此脚本。

libllama.so 来自 llama.rn 0.12.5 预编译，硬编码了：
  - DT_NEEDED libcdsprpc.so  → 非骁龙/鸿蒙设备没有此系统库
  - DT_NEEDED libOpenCL.so   → 旧 GPU 驱动缺少 OpenCL 3.0 符号
  - VERNEED libOpenCL.so     → 版本需求段，链接器校验 DT_NEEDED 匹配

此脚本将以上三条引用全部替换为空字符串，Bionic 动态链接器会静默跳过。

用法：
  python3 strip_dt_needed.py [path/to/libllama.so]
"""

import struct
import sys
import os

STRIP_LIBS = {'libcdsprpc.so', 'libOpenCL.so'}


def strip_dt_needed(path):
    if not os.path.exists(path):
        print(f"Error: {path} not found")
        return False

    with open(path, 'rb') as f:
        data = bytearray(f.read())

    if data[:4] != b'\x7fELF':
        print(f"Error: {path} is not an ELF file")
        return False

    # ── 解析 ELF header ──
    e_shoff = struct.unpack_from('<Q', data, 0x28)[0]
    e_shentsize, e_shnum, e_shstrndx = struct.unpack_from('<HHH', data, 0x3A)

    strtab_off = e_shoff + e_shstrndx * e_shentsize
    strtab_file_off = struct.unpack_from('<Q', data, strtab_off + 0x18)[0]

    def sh_name(ndx):
        end = data.find(b'\x00', strtab_file_off + ndx)
        return data[strtab_file_off + ndx:end].decode()

    dyn_off = dyn_size = 0
    dynstr_off = 0
    verneed_off = verneed_size = 0

    for i in range(e_shnum):
        off = e_shoff + i * e_shentsize
        name_ndx = struct.unpack_from('<I', data, off)[0]
        sh_file_off = struct.unpack_from('<Q', data, off + 0x18)[0]
        sh_size = struct.unpack_from('<Q', data, off + 0x20)[0]
        name = sh_name(name_ndx)
        if name == '.dynamic':
            dyn_off, dyn_size = sh_file_off, sh_size
        elif name == '.dynstr':
            dynstr_off = sh_file_off
        elif name == '.gnu.version_r':
            verneed_off, verneed_size = sh_file_off, sh_size

    changes = 0

    # ── 步骤 1：剥离 DT_NEEDED ──
    pos = dyn_off
    for i in range(dyn_size // 16):
        d_tag = struct.unpack_from('<q', data, pos)[0]
        if d_tag == 0:
            break
        if d_tag == 1:  # DT_NEEDED
            d_val = struct.unpack_from('<Q', data, pos + 8)[0]
            end = data.find(b'\x00', dynstr_off + d_val)
            name = data[dynstr_off + d_val:end].decode()
            if name in STRIP_LIBS:
                struct.pack_into('<Q', data, pos + 8, 0)
                changes += 1
                print(f"  DT_NEEDED: {name} → \"\"")
        pos += 16

    # ── 步骤 2：剥离 VERNEED ──
    # Elf64_Verneed: vn_version(2) vn_cnt(2) vn_file(4) vn_aux(4) vn_next(4)
    # vn_file 是 .dynstr 中的偏移，改为 0 即指向空字符串
    if verneed_off > 0:
        pos = verneed_off
        while pos < verneed_off + verneed_size:
            vn_version = struct.unpack_from('<H', data, pos)[0]
            if vn_version == 0:
                break
            vn_cnt, vn_file, vn_aux, vn_next = struct.unpack_from('<HIII', data, pos + 2)
            end = data.find(b'\x00', dynstr_off + vn_file)
            lib_name = data[dynstr_off + vn_file:end].decode()
            if lib_name in STRIP_LIBS:
                struct.pack_into('<I', data, pos + 4, 0)  # vn_file → 0
                changes += 1
                print(f"  VERNEED: {lib_name} → \"\"")
            if vn_next == 0:
                break
            pos += vn_next

    if changes == 0:
        print("  No target entries found (already stripped?)")
        return True

    with open(path, 'wb') as f:
        f.write(data)

    print(f"  Done. {changes} entries stripped, wrote {len(data)} bytes to {path}")
    return True


if __name__ == '__main__':
    path = sys.argv[1] if len(sys.argv) > 1 else 'app/src/main/jniLibs/arm64-v8a/libllama.so'
    print(f"Stripping DT_NEEDED + VERNEED from {path}")
    ok = strip_dt_needed(path)
    sys.exit(0 if ok else 1)