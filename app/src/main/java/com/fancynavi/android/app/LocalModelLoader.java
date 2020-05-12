package com.fancynavi.android.app;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;

class LocalModelLoader {

    private Obj obj;

    LocalModelLoader(Context context, int resourceId) {
        try {
            InputStream objStream = context.getResources().openRawResource(resourceId);
            obj = ObjReader.read(objStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    FloatBuffer getObjTexCoords() {
        FloatBuffer texCoords = ObjData.getTexCoords(obj, 2);
        FloatBuffer textCoordBuffer = FloatBuffer.allocate(texCoords.capacity());
        while (texCoords.hasRemaining()) {
            textCoordBuffer.put(texCoords.get());
        }
        return textCoordBuffer;

    }

    FloatBuffer getObjVertices() {
        FloatBuffer vertices = ObjData.getVertices(obj);
        FloatBuffer buff = FloatBuffer.allocate(vertices.capacity());
        while (vertices.hasRemaining()) {
            buff.put(vertices.get());
        }
        return buff;
    }

    IntBuffer getObjIndices() {
        IntBuffer indices = ObjData.getFaceVertexIndices(obj);
        IntBuffer vertIndicieBuffer = IntBuffer.allocate(indices.capacity());
        while (indices.hasRemaining()) {
            vertIndicieBuffer.put(indices.get());
        }
        return vertIndicieBuffer;
    }
}
