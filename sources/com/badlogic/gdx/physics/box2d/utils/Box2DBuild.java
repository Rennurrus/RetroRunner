package com.badlogic.gdx.physics.box2d.utils;

import com.badlogic.gdx.jnigen.AntScriptGenerator;
import com.badlogic.gdx.jnigen.BuildConfig;
import com.badlogic.gdx.jnigen.BuildTarget;
import com.badlogic.gdx.jnigen.NativeCodeGenerator;
import java.io.File;

public class Box2DBuild {
    public static void main(String[] args) throws Exception {
        BuildTarget win32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, false);
        BuildTarget win64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, true);
        BuildTarget lin32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, false);
        BuildTarget lin64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, true);
        BuildTarget android = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Android, false);
        BuildTarget mac64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.MacOsX, true);
        BuildTarget ios = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.IOS, false);
        NativeCodeGenerator nativeCodeGenerator = new NativeCodeGenerator();
        nativeCodeGenerator.generate("src", "bin" + File.pathSeparator + "../../../gdx/bin", "jni");
        new AntScriptGenerator().generate(new BuildConfig("gdx-box2d"), new BuildTarget[]{win32, win64, lin32, lin64, mac64, android, ios});
    }
}
