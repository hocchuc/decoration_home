package sg.vinova.decoration_home.arobject;

public class TextureChair {
    private static final String VERTEX_SHADER_SRC =
            "attribute vec4 a_position;\n" +
                    "attribute vec2 a_texCoord;\n" +
                    "varying vec2 v_texCoord;\n" +
                    "uniform mat4 u_mvpMatrix;\n" +
                    "void main()							\n" +
                    "{										\n" +
                    "	gl_Position = u_mvpMatrix * a_position;\n" +
                    "	v_texCoord = a_texCoord; 			\n" +
                    "}										\n";

    private static final String FRAGMENT_SHADER_SRC =
            "precision mediump float;\n" +
                    "varying vec2 v_texCoord;\n" +
                    "uniform sampler2D u_texture;\n" +

                    "void main(void)\n" +
                    "{\n" +
                    "	gl_FragColor = texture2D(u_texture, v_texCoord);\n" +
                    "}\n";

    private static final float[] VERTEX_BUF = {
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,

            // 2. Bottom face
            -0.5f, -0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,

            // 3. Front face
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,

            // 4. Right face
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f,  0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,

            // 5. Back face
            0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,

            // 6. Left face
            -0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
    };

    private static final float[] TEXTURE_COORD_BUF = {
            0.167f, 0.100f,
            0.833f, 0.100f,
            0.833f, 0.500f,
            0.833f, 0.500f,
            0.167f, 0.500f,
            0.167f, 0.100f,

            0.167f, 0.667f,
            0.833f, 0.667f,
            0.833f, 1.000f,
            0.833f, 1.000f,
            0.167f, 1.000f,
            0.167f, 0.667f,

            0.167f, 0.000f,
            0.833f, 0.000f,
            0.833f, 0.100f,
            0.833f, 0.100f,
            0.167f, 0.100f,
            0.167f, 0.000f,

            0.833f, 0.100f,
            1.000f, 0.100f,
            1.000f, 0.500f,
            1.000f, 0.500f,
            0.833f, 0.500f,
            0.833f, 0.100f,

            0.167f, 0.000f,
            0.833f, 0.000f,
            0.833f, 0.100f,
            0.833f, 0.100f,
            0.167f, 0.100f,
            0.167f, 0.000f,

            0.833f, 0.500f,
            0.833f, 0.100f,
            1.000f, 0.100f,
            1.000f, 0.100f,
            1.000f, 0.500f,
            0.833f, 0.500f,
    };

}
