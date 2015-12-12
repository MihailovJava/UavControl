package math

import static java.lang.Math.*

class Quaternion {

    float[] q = new float[4]

    public Quaternion(float psi,float theta,float gama){
        fromRotationMatrix(new Matrix(gama,theta,psi))
    }

    private fromRotationMatrix(Matrix m){
        float m00 = m.a.x;
        float m11 = m.b.y;
        float m22 = m.c.z;
        float m10 = m.b.x;
        float m01 = m.a.y;
        float m20 = m.c.x;
        float m02 = m.a.z;
        float m21 = m.c.y;
        float m12 = m.b.z;
        float qw;
        float qx;
        float qy;
        float qz;

        float tr = m00 + m11 + m22;

        if (tr > 0) {
            float S = (float)sqrt(tr+1) * 2;
            qw = 0.25f * S;
            qx = (m21 - m12) / S;
            qy = (m02 - m20) / S;
            qz = (m10 - m01) / S;
        } else if ((m00 > m11) && (m00 > m22)) {
            float S =(float) sqrt(1.0f + m00 - m11 - m22) * 2;
            qw = (m21 - m12) / S;
            qx = 0.25f * S;
            qy = (m01 + m10) / S;
            qz = (m02 + m20) / S;
        } else if (m11 > m22) {
            float S = (float) sqrt(1.0f + m11 - m00 - m22) * 2;
            qw = (m02 - m20) / S;
            qx = (m01 + m10) / S;
            qy = 0.25f * S;
            qz = (m12 + m21) / S;
        } else {
            float S = (float) sqrt(1.0f + m22 - m00 - m11) * 2;
            qw = (m10 - m01) / S;
            qx = (m02 + m20) / S;
            qy = (m12 + m21) / S;
            qz = 0.25f * S;
        }
        q[0] = qw;
        q[1] = qx;
        q[2] = qy;
        q[3] = qz;
    }

}

public class Vector {
    float x, y, z

}

public class Matrix{
    Vector a,b,c

    public Matrix(float roll, float pitch, float yaw){
        a = new Vector()
        b = new Vector()
        c = new Vector()
        float cp = cos(pitch);
        float sp = sin(pitch);
        float sr = sin(roll);
        float cr = cos(roll);
        float sy = sin(yaw);
        float cy = cos(yaw);

        a.x = cp * cy;
        a.y = (sr * sp * cy) - (cr * sy);
        a.z = (cr * sp * cy) + (sr * sy);
        b.x = cp * sy;
        b.y = (sr * sp * sy) + (cr * cy);
        b.z = (cr * sp * sy) - (sr * cy);
        c.x = -sp;
        c.y = sr * cp;
        c.z = cr * cp;
    }
}
