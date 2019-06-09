/**
 *  Copyright (C) 2018 Ryszard Wiśniewski <brut.alll@gmail.com>
 *  Copyright (C) 2018 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.example.apktool.brut.util;

import android.os.Build;

import java.util.Arrays;

public class OSDetection {
    private static String OS = System.getProperty("os.name").toLowerCase();

    private static String getBit() {
        if (isAndroid()) return determineArchName().toLowerCase();
        else return System.getProperty("sun.arch.data.model").toLowerCase();
    }

    private static String Bit = getBit();

    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    public static boolean isMacOSX() {
        return (OS.contains("mac"));
    }

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix") || (OS.contains("sunos")));
    }

    public static boolean is64Bit() {
        if (isAndroid()) return Bit.contains("64");
        else return Bit.equalsIgnoreCase("64");
    }

    public static String returnOS() {
        return OS;
    }

    public static String returnBit() {
        return Bit;
    }

    public static boolean isAndroid() {
        return "The Android Project".equals(System.getProperty("java.specification.vendor"));
    }

    private static String determineArchName() {
        // Note that we cannot use System.getProperty("os.arch") since that may give e.g. "aarch64"
        // while a 64-bit runtime may not be installed (like on the Samsung Galaxy S5 Neo).
        // Instead we search through the supported abi:s on the device, see:
        // http://developer.android.com/ndk/guides/abis.html
        // Note that we search for abi:s in preferred order (the ordering of the
        // Build.SUPPORTED_ABIS list) to avoid e.g. installing arm on an x86 system where arm
        // emulation is available.
        for (String androidArch : Build.SUPPORTED_ABIS) {
            switch (androidArch) {
                case "arm64-v8a": return "arm64-v8a";
                case "armeabi-v7a": return "armeabi-v7a";
                case "x86_64": return "x86_64";
                case "x86": return "x86";
            }
        }
        throw new RuntimeException("Unable to determine arch from Build.SUPPORTED_ABIS =  " +
                Arrays.toString(Build.SUPPORTED_ABIS));
    }

}
