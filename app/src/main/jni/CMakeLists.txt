cmake_minimum_required(VERSION 3.4.1)

# user defined repo location

add_definitions(-Oz)
#add_definitions(-std=c99)
add_definitions(-Wstrict-aliasing=2)
add_definitions(-ffunction-sections)
add_definitions(-fdata-sections)
#add_definitions(-mthumb)
#add_definitions(-mfloat-abi=softfp)
# define of DUK_OPT_DECLARE is placed in fb_script.h
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,--gc-sections")

file(GLOB SRCS nanovg/*.c gl_code.cpp)

add_library(
    gl2jni

    SHARED

    ${SRCS})

target_include_directories(gl2jni PUBLIC
    nanovg
    .
    )

find_library( log-lib log )
find_library( glesv2-lib GLESv2 )
target_link_libraries(gl2jni ${log-lib} ${glesv2-lib})
