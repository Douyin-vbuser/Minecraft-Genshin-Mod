package com.vbuser.particulate.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * ShaderProgram 类封装了OpenGL着色器程序的功能<br>
 * 提供着色器的创建、编译、链接和使用功能
 */
@SuppressWarnings("unused")
public class ShaderProgram {
    private final int programID;           // 着色器程序ID
    private final List<Integer> attachedShaders;  // 附加的着色器列表
    private boolean isDeleted = false;     // 标记是否已删除

    /**
     * 构造函数<br>
     * 创建新的着色器程序
     */
    public ShaderProgram() {
        programID = GL20.glCreateProgram();  // 创建程序对象
        if (programID == 0) {
            throw new RuntimeException("Could not create shader program");
        }
        attachedShaders = new ArrayList<>();
    }

    /**
     * 附加顶点着色器
     * @param resource 顶点着色器资源位置
     * @throws IOException 如果读取资源失败
     */
    public void attachVertexShader(ResourceLocation resource) throws IOException {
        int vertexShaderID = createShader(resource, GL20.GL_VERTEX_SHADER);  // 创建顶点着色器
        GL20.glAttachShader(programID, vertexShaderID);  // 附加到程序
        attachedShaders.add(vertexShaderID);
    }

    /**
     * 附加片段着色器
     * @param resource 片段着色器资源位置
     * @throws IOException 如果读取资源失败
     */
    public void attachFragmentShader(ResourceLocation resource) throws IOException {
        int fragmentShaderID = createShader(resource, GL20.GL_FRAGMENT_SHADER);  // 创建片段着色器
        GL20.glAttachShader(programID, fragmentShaderID);  // 附加到程序
        attachedShaders.add(fragmentShaderID);
    }

    /**
     * 链接着色器程序<br>
     * 将附加的着色器链接为可执行程序
     */
    public void link() {
        GL20.glLinkProgram(programID);  // 链接程序
        int status = GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS);  // 检查链接状态

        if (status == GL11.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(programID, 1024);  // 获取错误日志
            throw new RuntimeException("Error linking shader program: " + log);
        }

        // 分离和删除着色器对象（不再需要）
        for (int shaderID : attachedShaders) {
            GL20.glDetachShader(programID, shaderID);
            GL20.glDeleteShader(shaderID);
        }
        attachedShaders.clear();

        // 验证程序
        GL20.glValidateProgram(programID);
        if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(programID, 1024);  // 获取警告日志
            System.err.println("Warning validating shader program: " + log);
        }
    }

    /**
     * 使用着色器程序<br>
     * 如果程序未被删除，则将其设为当前活动程序
     */
    public void use() {
        if (!isDeleted) {
            OpenGlHelper.glUseProgram(programID);
        }
    }

    /**
     * 释放着色器程序<br>
     * 将当前活动程序设为0（无程序）
     */
    public void release() {
        OpenGlHelper.glUseProgram(0);
    }

    /**
     * 删除着色器程序<br>
     * 释放所有相关资源
     */
    public void delete() {
        release();
        if (!isDeleted) {
            // 分离和删除所有着色器
            for (int shaderID : attachedShaders) {
                GL20.glDetachShader(programID, shaderID);
                GL20.glDeleteShader(shaderID);
            }
            attachedShaders.clear();

            // 删除程序
            GL20.glDeleteProgram(programID);
            isDeleted = true;
        }
    }

    /**
     * 设置浮点型统一变量
     * @param name 变量名称
     * @param value 变量值
     */
    public void setUniform1f(String name, float value) {
        if (!isDeleted) {
            int location = GL20.glGetUniformLocation(programID, name);  // 获取变量位置
            if (location != -1) {
                GL20.glUniform1f(location, value);  // 设置变量值
            }
        }
    }

    /**
     * 设置二维浮点向量统一变量
     * @param name 变量名称
     * @param x X分量
     * @param y Y分量
     */
    public void setUniform2f(String name, float x, float y) {
        if (!isDeleted) {
            int location = GL20.glGetUniformLocation(programID, name);
            if (location != -1) {
                GL20.glUniform2f(location, x, y);
            }
        }
    }

    /**
     * 设置三维浮点向量统一变量
     * @param name 变量名称
     * @param x X分量
     * @param y Y分量
     * @param z Z分量
     */
    public void setUniform3f(String name, float x, float y, float z) {
        if (!isDeleted) {
            int location = GL20.glGetUniformLocation(programID, name);
            if (location != -1) {
                GL20.glUniform3f(location, x, y, z);
            }
        }
    }

    /**
     * 设置四维浮点向量统一变量
     * @param name 变量名称
     * @param x X分量
     * @param y Y分量
     * @param z Z分量
     * @param w W分量
     */
    public void setUniform4f(String name, float x, float y, float z, float w) {
        if (!isDeleted) {
            int location = GL20.glGetUniformLocation(programID, name);
            if (location != -1) {
                GL20.glUniform4f(location, x, y, z, w);
            }
        }
    }

    /**
     * 设置整型统一变量
     * @param name 变量名称
     * @param value 变量值
     */
    public void setUniform1i(String name, int value) {
        if (!isDeleted) {
            int location = GL20.glGetUniformLocation(programID, name);
            if (location != -1) {
                GL20.glUniform1i(location, value);
            }
        }
    }

    /**
     * 创建着色器对象
     * @param resource 着色器资源位置
     * @param shaderType 着色器类型
     * @return 着色器对象ID
     * @throws IOException 如果读取资源失败
     */
    private int createShader(ResourceLocation resource, int shaderType) throws IOException {
        int shader = GL20.glCreateShader(shaderType);  // 创建着色器对象
        if (shader == 0) {
            throw new RuntimeException("Could not create shader of type " + shaderType);
        }

        // 读取着色器源代码
        String shaderSource;
        try (IResource res = Minecraft.getMinecraft().getResourceManager().getResource(resource);
             InputStream inputStream = res.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder shaderSourceBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSourceBuilder.append(line).append("\n");
            }
            shaderSource = shaderSourceBuilder.toString();
        }

        // 设置源代码并编译
        GL20.glShaderSource(shader, shaderSource);
        GL20.glCompileShader(shader);

        // 检查编译状态
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shader, 1024);  // 获取编译错误
            GL20.glDeleteShader(shader);  // 删除着色器
            throw new RuntimeException("Error compiling shader (" +
                    resource + "): " + log);
        }

        return shader;
    }

    /**
     * 获取程序ID
     * @return 程序ID
     */
    public int getProgramID() {
        return programID;
    }

    /**
     * 检查程序是否已删除
     * @return 如果已删除返回true
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * 析构函数<br>
     * 确保程序被正确删除
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            if (!isDeleted) {
                delete();  // 确保程序被删除
            }
        } finally {
            super.finalize();
        }
    }
}