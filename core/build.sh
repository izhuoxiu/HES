# mac/Linux版本
#!/bin/bash
emcc mfs_core.cpp \
-O3 \
-std=c++17 \
-s WASM=1 \
-s EXPORTED_FUNCTIONS='["mfs_set_callback","mfs_load_script","mfs_tick","mfs_reset"]' \
-s EXPORTED_RUNTIME_METHODS='["ccall","cwrap"]' \
-s RESERVED_FUNCTION_POINTERS=20 \
-s ALLOW_TABLE_GROWTH=1 \
-s NO_FILESYSTEM=1 \
-s OPTIMIZE=1 \
-o mfs.js

echo "编译完成，输出 mfs.js mfs.wasm"
