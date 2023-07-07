package com.badlogic.gdx.utils;

import com.badlogic.gdx.jnigen.AntScriptGenerator;
import com.badlogic.gdx.jnigen.BuildTarget;
import com.badlogic.gdx.jnigen.NativeCodeGenerator;
import com.twi.game.BuildConfig;

public class GdxBuild {
    public static void main(String[] args) throws Exception {
        new NativeCodeGenerator().generate("src", "bin", "jni", new String[]{"**/*"}, (String[]) null);
        String[] excludeCpp = {"iosgl/**"};
        BuildTarget win32home = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, false);
        win32home.compilerPrefix = BuildConfig.FLAVOR;
        win32home.buildFileName = "build-windows32home.xml";
        win32home.excludeFromMasterBuildFile = true;
        win32home.cppExcludes = excludeCpp;
        BuildTarget win32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, false);
        win32.cppExcludes = excludeCpp;
        BuildTarget win64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, true);
        win64.cppExcludes = excludeCpp;
        BuildTarget lin32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, false);
        lin32.cppExcludes = excludeCpp;
        BuildTarget lin64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, true);
        lin64.cppExcludes = excludeCpp;
        BuildTarget android = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Android, false);
        android.linkerFlags += " -llog";
        android.cppExcludes = excludeCpp;
        BuildTarget mac64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.MacOsX, true);
        mac64.cppExcludes = excludeCpp;
        BuildTarget ios = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.IOS, false);
        ios.headerDirs = new String[]{"iosgl"};
        new AntScriptGenerator().generate(new com.badlogic.gdx.jnigen.BuildConfig("gdx", "../target/native", "libs", "jni"), new BuildTarget[]{mac64, win32home, win32, win64, lin32, lin64, android, ios});
    }
}
