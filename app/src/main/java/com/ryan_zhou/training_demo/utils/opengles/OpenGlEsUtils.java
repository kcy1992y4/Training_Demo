package com.ryan_zhou.training_demo.utils.opengles;

import android.opengl.GLES20;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/26 16:29
 * @copyright TCL-MIE
 */
public class OpenGlEsUtils {

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
