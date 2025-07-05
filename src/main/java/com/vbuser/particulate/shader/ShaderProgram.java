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

@SuppressWarnings("unused")
public class ShaderProgram {
    private final int programID;
    private final List<Integer> attachedShaders;
    private boolean isDeleted = false;

    public ShaderProgram() {
        programID = GL20.glCreateProgram();
        if (programID == 0) {
            throw new RuntimeException("Could not create shader program");
        }
        attachedShaders = new ArrayList<>();
    }

    public void attachVertexShader(ResourceLocation resource) throws IOException {
        int vertexShaderID = createShader(resource, GL20.GL_VERTEX_SHADER);
        GL20.glAttachShader(programID, vertexShaderID);
        attachedShaders.add(vertexShaderID);
    }

    public void attachFragmentShader(ResourceLocation resource) throws IOException {
        int fragmentShaderID = createShader(resource, GL20.GL_FRAGMENT_SHADER);
        GL20.glAttachShader(programID, fragmentShaderID);
        attachedShaders.add(fragmentShaderID);
    }

    public void link() {
        GL20.glLinkProgram(programID);
        int status = GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS);

        if (status == GL11.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(programID, 1024);
            throw new RuntimeException("Error linking shader program: " + log);
        }

        for (int shaderID : attachedShaders) {
            GL20.glDetachShader(programID, shaderID);
            GL20.glDeleteShader(shaderID);
        }
        attachedShaders.clear();

        GL20.glValidateProgram(programID);
        if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(programID, 1024);
            System.err.println("Warning validating shader program: " + log);
        }
    }

    public void use() {
        if (!isDeleted) {
            OpenGlHelper.glUseProgram(programID);
        }
    }

    public void release() {
        OpenGlHelper.glUseProgram(0);
    }

    public void delete() {
        release();
        if (!isDeleted) {
            for (int shaderID : attachedShaders) {
                GL20.glDetachShader(programID, shaderID);
                GL20.glDeleteShader(shaderID);
            }
            attachedShaders.clear();

            GL20.glDeleteProgram(programID);
            isDeleted = true;
        }
    }

    public void setUniform1f(String name, float value) {
        if (!isDeleted) {
            int location = GL20.glGetUniformLocation(programID, name);
            if (location != -1) {
                GL20.glUniform1f(location, value);
            }
        }
    }

    public void setUniform2f(String name, float x, float y) {
        if (!isDeleted) {
            int location = GL20.glGetUniformLocation(programID, name);
            if (location != -1) {
                GL20.glUniform2f(location, x, y);
            }
        }
    }

    public void setUniform3f(String name, float x, float y, float z) {
        if (!isDeleted) {
            int location = GL20.glGetUniformLocation(programID, name);
            if (location != -1) {
                GL20.glUniform3f(location, x, y, z);
            }
        }
    }

    public void setUniform4f(String name, float x, float y, float z, float w) {
        if (!isDeleted) {
            int location = GL20.glGetUniformLocation(programID, name);
            if (location != -1) {
                GL20.glUniform4f(location, x, y, z, w);
            }
        }
    }

    public void setUniform1i(String name, int value) {
        if (!isDeleted) {
            int location = GL20.glGetUniformLocation(programID, name);
            if (location != -1) {
                GL20.glUniform1i(location, value);
            }
        }
    }

    private int createShader(ResourceLocation resource, int shaderType) throws IOException {
        int shader = GL20.glCreateShader(shaderType);
        if (shader == 0) {
            throw new RuntimeException("Could not create shader of type " + shaderType);
        }

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

        GL20.glShaderSource(shader, shaderSource);
        GL20.glCompileShader(shader);

        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shader, 1024);
            GL20.glDeleteShader(shader);
            throw new RuntimeException("Error compiling shader (" +
                    resource + "): " + log);
        }

        return shader;
    }

    public int getProgramID() {
        return programID;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (!isDeleted) {
                delete();
            }
        } finally {
            super.finalize();
        }
    }
}
