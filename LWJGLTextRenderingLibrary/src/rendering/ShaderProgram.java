package rendering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import rendering.uniforms.Uniform;

public class ShaderProgram {

	private final int programID;

	public ShaderProgram(String vertexFile, String fragmentFile, String... inVariables) {
		int vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		int fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		programID = GL20.glCreateProgram();
		if (programID == 0) {
			throw new RuntimeException("Could not get valid id for shader program.");
		}
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes(inVariables);
		GL20.glLinkProgram(programID);
		int linkStatus = GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS);
		if (linkStatus == 0) {
			int infoLength = GL20.glGetProgrami(programID, GL20.GL_INFO_LOG_LENGTH);
			String errorMessage = GL20.glGetProgramInfoLog(programID, infoLength);
			throw new RuntimeException("Could not link shaders. " + errorMessage);
		}
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
	}

	private void bindAttributes(String[] inVariables) {
		for (int i = 0; i < inVariables.length; i++) {
			GL20.glBindAttribLocation(programID, i, inVariables[i]);
		}
	}

	public void stop() {
		GL20.glUseProgram(0);
	}

	public static int loadShader(String shaderFile, int shaderType) {
		StringBuilder shaderSource = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(ShaderProgram.class.getResourceAsStream(shaderFile)));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append(System.lineSeparator());
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int shaderID = GL20.glCreateShader(shaderType);
		if (shaderID != 0) {
			GL20.glShaderSource(shaderID, shaderSource);
			GL20.glCompileShader(shaderID);
		} else {
			throw new RuntimeException("Can't get valid shader ID.");
		}
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			String infoLog = GL20.glGetShaderInfoLog(shaderID, GL20.glGetShaderi(shaderID, GL20.GL_INFO_LOG_LENGTH));
			System.out.println("Couldn't compile shader " + shaderFile);
			System.out.println(infoLog);
			System.exit(-1);
		}
		return shaderID;
	}

	public void start() {
		GL20.glUseProgram(programID);
	}

	public void storeUniformLocations(Uniform... uniforms) {
		for (Uniform uniform : uniforms) {
			uniform.storeUniformLocation(programID);
		}
		GL20.glValidateProgram(programID);
	}
	
	public void delete() {
		GL20.glDeleteProgram(programID);
	}
}
