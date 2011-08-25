package org.anddev.andengine.opengl.util;

import java.nio.Buffer;

import org.anddev.andengine.engine.options.RenderOptions;
import org.anddev.andengine.opengl.shader.util.constants.ShaderPrograms;
import org.anddev.andengine.opengl.texture.Texture.PixelFormat;
import org.anddev.andengine.opengl.util.GLMatrixStacks.MatrixMode;
import org.anddev.andengine.util.Debug;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLUtils;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 18:00:43 - 08.03.2010
 */
public class GLState {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int[] HARDWAREID_CONTAINER = new int[1];

	// ===========================================================
	// Fields
	// ===========================================================

	private static GLMatrixStacks sGLMatrixStacks = new GLMatrixStacks();

	private static int sCurrentHardwareBufferID = -1;
	private static int sCurrentShaderProgramID = -1;
	private static int sCurrentHardwareTextureID = -1;

	private static int sCurrentSourceBlendMode = -1;
	private static int sCurrentDestinationBlendMode = -1;

	private static boolean sEnableDither = true;
	private static boolean sEnableDepthTest = true;

	private static boolean sEnableScissorTest = false;
	private static boolean sEnableBlend = false;
	private static boolean sEnableCulling = false;
	private static boolean sEnableTextures = false;

	private static float sLineWidth = 1;

	// ===========================================================
	// Methods
	// ===========================================================

	public static void reset() {
		GLState.sGLMatrixStacks.reset();

		GLState.sCurrentHardwareBufferID = -1;
		GLState.sCurrentShaderProgramID = -1;
		GLState.sCurrentHardwareTextureID = -1;

		GLState.sCurrentSourceBlendMode = -1;
		GLState.sCurrentDestinationBlendMode = -1;

		GLState.enableDither();
		GLState.enableDepthTest();

		GLState.disableBlend();
		GLState.disableCulling();
		GLState.disableTextures();

		GLES20.glEnableVertexAttribArray(ShaderPrograms.ATTRIBUTE_POSITION_LOCATION);
		GLES20.glEnableVertexAttribArray(ShaderPrograms.ATTRIBUTE_COLOR_LOCATION);
		GLES20.glEnableVertexAttribArray(ShaderPrograms.ATTRIBUTE_TEXTURECOORDINATES_LOCATION);

		GLState.sLineWidth = 1;
	}

	public static void enableExtensions(final RenderOptions pRenderOptions) {
		final String version = GLES20.glGetString(GLES20.GL_VERSION);
		final String renderer = GLES20.glGetString(GLES20.GL_RENDERER);
		final String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);

		Debug.d("RENDERER: " + renderer);
		Debug.d("VERSION: " + version);
		Debug.d("EXTENSIONS: " + extensions);
	}

	public static void enableScissorTest() {
		if(!GLState.sEnableScissorTest) {
			GLState.sEnableScissorTest = true;
			GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
		}
	}
	public static void disableScissorTest() {
		if(GLState.sEnableScissorTest) {
			GLState.sEnableScissorTest = false;
			GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
		}
	}

	public static void enableBlend() {
		if(!GLState.sEnableBlend) {
			GLState.sEnableBlend = true;
			GLES20.glEnable(GLES20.GL_BLEND);
		}
	}
	public static void disableBlend() {
		if(GLState.sEnableBlend) {
			GLState.sEnableBlend = false;
			GLES20.glDisable(GLES20.GL_BLEND);
		}
	}

	public static void enableCulling() {
		if(!GLState.sEnableCulling) {
			GLState.sEnableCulling = true;
			GLES20.glEnable(GLES20.GL_CULL_FACE);
		}
	}
	public static void disableCulling() {
		if(GLState.sEnableCulling) {
			GLState.sEnableCulling = false;
			GLES20.glDisable(GLES20.GL_CULL_FACE);
		}
	}

	public static void enableTextures() {
		if(!GLState.sEnableTextures) {
			GLState.sEnableTextures = true;
			GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		}
	}
	public static void disableTextures() {
		if(GLState.sEnableTextures) {
			GLState.sEnableTextures = false;
			GLES20.glDisable(GLES20.GL_TEXTURE_2D);
		}
	}

	public static void enableDither() {
		if(!GLState.sEnableDither) {
			GLState.sEnableDither = true;
			GLES20.glEnable(GLES20.GL_DITHER);
		}
	}
	public static void disableDither() {
		if(GLState.sEnableDither) {
			GLState.sEnableDither = false;
			GLES20.glDisable(GLES20.GL_DITHER);
		}
	}

	public static void enableDepthTest() {
		if(!GLState.sEnableDepthTest) {
			GLState.sEnableDepthTest = true;
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		}
	}
	public static void disableDepthTest() {
		if(GLState.sEnableDepthTest) {
			GLState.sEnableDepthTest = false;
			GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		}
	}

	public static void bindBuffer(final int pHardwareBufferID) {
		/* Reduce unnecessary buffer switching calls. */
		if(GLState.sCurrentHardwareBufferID != pHardwareBufferID) {
			GLState.sCurrentHardwareBufferID = pHardwareBufferID;
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, pHardwareBufferID);
		}
	}

	public static void deleteBuffer(final int pHardwareBufferID) {
		if(GLState.sCurrentHardwareBufferID == pHardwareBufferID) {
			GLState.sCurrentHardwareBufferID = -1;
		}
		GLState.HARDWAREID_CONTAINER[0] = pHardwareBufferID;
		GLES20.glDeleteBuffers(1, GLState.HARDWAREID_CONTAINER, 0);
	}

	public static int generateBuffer() {
		GLES20.glGenBuffers(1, GLState.HARDWAREID_CONTAINER, 0);
		return GLState.HARDWAREID_CONTAINER[0];
	}

	public static int generateBuffer(final int pSize, final int pUsage) {
		GLES20.glGenBuffers(1, GLState.HARDWAREID_CONTAINER, 0);
		final int hardwareBufferID = GLState.HARDWAREID_CONTAINER[0];

		GLState.bindBuffer(hardwareBufferID);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, pSize, null, pUsage);
		GLState.bindBuffer(0);

		return hardwareBufferID;
	}

	public static void useProgram(final int pShaderProgramID) {
		/* Reduce unnecessary shader switching calls. */
		if(GLState.sCurrentShaderProgramID != pShaderProgramID) {
			GLState.sCurrentShaderProgramID = pShaderProgramID;
			GLES20.glUseProgram(pShaderProgramID);
		}
	}

	public static void deleteProgram(final int pShaderProgramID) {
		if(GLState.sCurrentShaderProgramID == pShaderProgramID) {
			GLState.sCurrentShaderProgramID = -1;
		}
		GLES20.glDeleteProgram(pShaderProgramID);
	}

	public static int generateTexture() {
		GLES20.glGenTextures(1, GLState.HARDWAREID_CONTAINER, 0);
		return GLState.HARDWAREID_CONTAINER[0];
	}

	/**
	 * @see {@link GLState#forceBindTexture(GLES20, int)}
	 * @param GLES20
	 * @param pHardwareTextureID
	 */
	public static void bindTexture(final int pHardwareTextureID) {
		/* Reduce unnecessary texture switching calls. */
		if(GLState.sCurrentHardwareTextureID != pHardwareTextureID) {
			GLState.sCurrentHardwareTextureID = pHardwareTextureID;
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, pHardwareTextureID);
		}
	}

	public static void deleteTexture(final int pHardwareTextureID) {
		if(GLState.sCurrentHardwareTextureID == pHardwareTextureID) {
			GLState.sCurrentHardwareTextureID = -1;
		}
		GLState.HARDWAREID_CONTAINER[0] = pHardwareTextureID;
		GLES20.glDeleteTextures(1, GLState.HARDWAREID_CONTAINER, 0);
	}

	public static void blendFunction(final int pSourceBlendMode, final int pDestinationBlendMode) {
		if(GLState.sCurrentSourceBlendMode != pSourceBlendMode || GLState.sCurrentDestinationBlendMode != pDestinationBlendMode) {
			GLState.sCurrentSourceBlendMode = pSourceBlendMode;
			GLState.sCurrentDestinationBlendMode = pDestinationBlendMode;
			GLES20.glBlendFunc(pSourceBlendMode, pDestinationBlendMode);
		}
	}

	public static void lineWidth(final float pLineWidth) {
		if(GLState.sLineWidth  != pLineWidth) {
			GLState.sLineWidth = pLineWidth;
			GLES20.glLineWidth(pLineWidth);
		}
	}

	public static void switchToModelViewMatrix() {
		GLState.sGLMatrixStacks.setMatrixMode(MatrixMode.MODELVIEW);
	}

	public static void switchToProjectionMatrix() {
		GLState.sGLMatrixStacks.setMatrixMode(MatrixMode.PROJECTION);
	}

	public static void switchToMatrix(final MatrixMode pMatrixMode) {
		GLState.sGLMatrixStacks.setMatrixMode(pMatrixMode);
	}

	public static float[] getProjectionMatrix() {
		return GLState.sGLMatrixStacks.getProjectionGLMatrix();
	}

	public static float[] getModelViewMatrix() {
		return GLState.sGLMatrixStacks.getModelViewGLMatrix();
	}

	public static float[] getModelViewProjectionMatrix() {
		return GLState.sGLMatrixStacks.getModelViewProjectionGLMatrix();
	}

	public static void setProjectionIdentityMatrix() {
		GLState.switchToProjectionMatrix();
		GLState.sGLMatrixStacks.glLoadIdentity();
	}

	public static void setModelViewIdentityMatrix() {
		GLState.switchToModelViewMatrix();
		GLState.sGLMatrixStacks.glLoadIdentity();
	}

	public static void glLoadIdentity() {
		GLState.sGLMatrixStacks.glLoadIdentity();
	}

	public static void glPushMatrix() {
		GLState.sGLMatrixStacks.glPushMatrix();
	}

	public static void glPopMatrix() {
		GLState.sGLMatrixStacks.glPopMatrix();
	}

	public static void glTranslatef(final float pX, final float pY, final int pZ) {
		GLState.sGLMatrixStacks.glTranslatef(pX, pY, pZ);
	}

	public static void glRotatef(final float pAngle, final float pX, final float pY, final int pZ) {
		GLState.sGLMatrixStacks.glRotatef(pAngle, pX, pY, pZ);
	}

	public static void glScalef(final float pScaleX, final float pScaleY, final int pScaleZ) {
		GLState.sGLMatrixStacks.glScalef(pScaleX, pScaleY, pScaleZ);
	}

	public static void glOrthof(final float pLeft, final float pRight, final float pBottom, final float pTop, final float pZNear, final float pZFar) {
		GLState.sGLMatrixStacks.glOrthof(pLeft, pRight, pBottom, pTop, pZNear, pZFar);
	}

	/**
	 * <b>Note:</b> does not pre-multiply the alpha channel!</br>
	 * Except that difference, same as: {@link GLUtils#texSubImage2D(int, int, int, int, Bitmap, int, int)}</br>
	 * </br>
	 * See topic: '<a href="http://groups.google.com/group/android-developers/browse_thread/thread/baa6c33e63f82fca">PNG loading that doesn't premultiply alpha?</a>'
	 * @param pBorder
	 */
	public static void glTexImage2D(final int pTarget, final int pLevel, final Bitmap pBitmap, final int pBorder, final PixelFormat pPixelFormat) {
		final Buffer pixelBuffer = GLHelper.getPixels(pBitmap, pPixelFormat);

		GLES20.glTexImage2D(pTarget, pLevel, pPixelFormat.getGLFormat(), pBitmap.getWidth(), pBitmap.getHeight(), pBorder, pPixelFormat.getGLFormat(), pPixelFormat.getGLType(), pixelBuffer);
	}

	/**
	 * <b>Note:</b> does not pre-multiply the alpha channel!</br>
	 * Except that difference, same as: {@link GLUtils#texSubImage2D(int, int, int, int, Bitmap, int, int)}</br>
	 * </br>
	 * See topic: '<a href="http://groups.google.com/group/android-developers/browse_thread/thread/baa6c33e63f82fca">PNG loading that doesn't premultiply alpha?</a>'
	 */
	public static void glTexSubImage2D(final int pTarget, final int pLevel, final int pX, final int pY, final Bitmap pBitmap, final PixelFormat pPixelFormat) {
		final Buffer pixelBuffer = GLHelper.getPixels(pBitmap, pPixelFormat);

		GLES20.glTexSubImage2D(pTarget, pLevel, pX, pY, pBitmap.getWidth(), pBitmap.getHeight(), pPixelFormat.getGLFormat(), pPixelFormat.getGLType(), pixelBuffer);
	}

	public static int getGLError() {
		return GLES20.glGetError();
	}

	public static void checkGLError() throws GLException { // TODO Use more often!
		final int err = GLES20.glGetError();
		if (err != GLES20.GL_NO_ERROR) {
			throw new GLException(err);
		}
	}

	public static void clearGLError() {
		GLES20.glGetError();
	}

	public static int getFrameBufferStatus() {
		return GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
	}

	public static void checkFrameBufferStatus() {
		final int status = GLState.getFrameBufferStatus();
		if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
			throw new GLException(status);
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
