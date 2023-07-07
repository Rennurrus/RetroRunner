package com.badlogic.gdx.physics.box2d;

public class JointDef {
    public Body bodyA = null;
    public Body bodyB = null;
    public boolean collideConnected = false;
    public JointType type = JointType.Unknown;

    public enum JointType {
        Unknown(0),
        RevoluteJoint(1),
        PrismaticJoint(2),
        DistanceJoint(3),
        PulleyJoint(4),
        MouseJoint(5),
        GearJoint(6),
        WheelJoint(7),
        WeldJoint(8),
        FrictionJoint(9),
        RopeJoint(10),
        MotorJoint(11);
        
        public static JointType[] valueTypes;
        private int value;

        static {
            JointType jointType;
            JointType jointType2;
            JointType jointType3;
            JointType jointType4;
            JointType jointType5;
            JointType jointType6;
            JointType jointType7;
            JointType jointType8;
            JointType jointType9;
            JointType jointType10;
            JointType jointType11;
            JointType jointType12;
            valueTypes = new JointType[]{jointType, jointType2, jointType3, jointType4, jointType5, jointType6, jointType7, jointType8, jointType9, jointType10, jointType11, jointType12};
        }

        private JointType(int value2) {
            this.value = value2;
        }

        public int getValue() {
            return this.value;
        }
    }
}
