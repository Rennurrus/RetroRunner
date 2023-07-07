package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.FrictionJoint;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.physics.box2d.joints.GearJoint;
import com.badlogic.gdx.physics.box2d.joints.GearJointDef;
import com.badlogic.gdx.physics.box2d.joints.MotorJoint;
import com.badlogic.gdx.physics.box2d.joints.MotorJointDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.PulleyJoint;
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import java.util.Iterator;

public final class World implements Disposable {
    protected final long addr;
    protected final LongMap<Body> bodies = new LongMap<>(100);
    private final Contact contact = new Contact(this, 0);
    private long[] contactAddrs = new long[HttpStatus.SC_OK];
    protected ContactFilter contactFilter = null;
    protected ContactListener contactListener = null;
    private final Array<Contact> contacts = new Array<>();
    protected final LongMap<Fixture> fixtures = new LongMap<>(100);
    protected final Pool<Body> freeBodies = new Pool<Body>(100, HttpStatus.SC_OK) {
        /* access modifiers changed from: protected */
        public Body newObject() {
            return new Body(World.this, 0);
        }
    };
    private final Array<Contact> freeContacts = new Array<>();
    protected final Pool<Fixture> freeFixtures = new Pool<Fixture>(100, HttpStatus.SC_OK) {
        /* access modifiers changed from: protected */
        public Fixture newObject() {
            return new Fixture((Body) null, 0);
        }
    };
    final Vector2 gravity = new Vector2();
    private final ContactImpulse impulse = new ContactImpulse(this, 0);
    protected final LongMap<Joint> joints = new LongMap<>(100);
    private final Manifold manifold = new Manifold(0);
    private QueryCallback queryCallback = null;
    private RayCastCallback rayCastCallback = null;
    private Vector2 rayNormal = new Vector2();
    private Vector2 rayPoint = new Vector2();
    final float[] tmpGravity = new float[2];

    public static native float getVelocityThreshold();

    private native void jniClearForces(long j);

    private native long jniCreateBody(long j, int i, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, float f9);

    private native long jniCreateDistanceJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5, float f6, float f7);

    private native long jniCreateFrictionJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5, float f6);

    private native long jniCreateGearJoint(long j, long j2, long j3, boolean z, long j4, long j5, float f);

    private native long jniCreateMotorJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5, float f6);

    private native long jniCreateMouseJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5);

    private native long jniCreatePrismaticJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5, float f6, float f7, boolean z2, float f8, float f9, boolean z3, float f10, float f11);

    private native long jniCreatePulleyJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11);

    private native long jniCreateRevoluteJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5, boolean z2, float f6, float f7, boolean z3, float f8, float f9);

    private native long jniCreateRopeJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5);

    private native long jniCreateWeldJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5, float f6, float f7);

    private native long jniCreateWheelJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5, float f6, boolean z2, float f7, float f8, float f9, float f10);

    private native void jniDeactivateBody(long j, long j2);

    private native void jniDestroyBody(long j, long j2);

    private native void jniDestroyFixture(long j, long j2, long j3);

    private native void jniDestroyJoint(long j, long j2);

    private native void jniDispose(long j);

    private native boolean jniGetAutoClearForces(long j);

    private native int jniGetBodyCount(long j);

    private native int jniGetContactCount(long j);

    private native void jniGetContactList(long j, long[] jArr);

    private native void jniGetGravity(long j, float[] fArr);

    private native int jniGetJointcount(long j);

    private native int jniGetProxyCount(long j);

    private native boolean jniIsLocked(long j);

    private native void jniQueryAABB(long j, float f, float f2, float f3, float f4);

    private native void jniRayCast(long j, float f, float f2, float f3, float f4);

    private native void jniSetAutoClearForces(long j, boolean z);

    private native void jniSetContiousPhysics(long j, boolean z);

    private native void jniSetGravity(long j, float f, float f2);

    private native void jniSetWarmStarting(long j, boolean z);

    private native void jniStep(long j, float f, int i, int i2);

    private native long newWorld(float f, float f2, boolean z);

    private native void setUseDefaultContactFilter(boolean z);

    public static native void setVelocityThreshold(float f);

    static {
        new SharedLibraryLoader().load("gdx-box2d");
    }

    public World(Vector2 gravity2, boolean doSleep) {
        this.addr = newWorld(gravity2.x, gravity2.y, doSleep);
        this.contacts.ensureCapacity(this.contactAddrs.length);
        this.freeContacts.ensureCapacity(this.contactAddrs.length);
        for (int i = 0; i < this.contactAddrs.length; i++) {
            this.freeContacts.add(new Contact(this, 0));
        }
    }

    public void setDestructionListener(DestructionListener listener) {
    }

    public void setContactFilter(ContactFilter filter) {
        this.contactFilter = filter;
        setUseDefaultContactFilter(filter == null);
    }

    public void setContactListener(ContactListener listener) {
        this.contactListener = listener;
    }

    public Body createBody(BodyDef def) {
        BodyDef bodyDef = def;
        long j = this.addr;
        BodyDef bodyDef2 = bodyDef;
        long j2 = j;
        long bodyAddr = jniCreateBody(j2, bodyDef.type.getValue(), bodyDef.position.x, bodyDef.position.y, bodyDef.angle, bodyDef.linearVelocity.x, bodyDef.linearVelocity.y, bodyDef.angularVelocity, bodyDef.linearDamping, bodyDef.angularDamping, bodyDef.allowSleep, bodyDef.awake, bodyDef.fixedRotation, bodyDef2.bullet, bodyDef2.active, bodyDef2.gravityScale);
        Body body = this.freeBodies.obtain();
        body.reset(bodyAddr);
        this.bodies.put(body.addr, body);
        return body;
    }

    public void destroyBody(Body body) {
        Array<JointEdge> jointList = body.getJointList();
        while (jointList.size > 0) {
            destroyJoint(body.getJointList().get(0).joint);
        }
        jniDestroyBody(this.addr, body.addr);
        body.setUserData((Object) null);
        this.bodies.remove(body.addr);
        Array<Fixture> fixtureList = body.getFixtureList();
        while (fixtureList.size > 0) {
            Fixture fixtureToDelete = fixtureList.removeIndex(0);
            this.fixtures.remove(fixtureToDelete.addr).setUserData((Object) null);
            this.freeFixtures.free(fixtureToDelete);
        }
        this.freeBodies.free(body);
    }

    /* access modifiers changed from: package-private */
    public void destroyFixture(Body body, Fixture fixture) {
        jniDestroyFixture(this.addr, body.addr, fixture.addr);
    }

    /* access modifiers changed from: package-private */
    public void deactivateBody(Body body) {
        jniDeactivateBody(this.addr, body.addr);
    }

    public Joint createJoint(JointDef def) {
        long jointAddr = createProperJoint(def);
        Joint joint = null;
        if (def.type == JointDef.JointType.DistanceJoint) {
            joint = new DistanceJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.FrictionJoint) {
            joint = new FrictionJoint(this, jointAddr);
        }
        Joint joint2 = joint;
        if (def.type == JointDef.JointType.GearJoint) {
            joint2 = new GearJoint(this, jointAddr, ((GearJointDef) def).joint1, ((GearJointDef) def).joint2);
        }
        if (def.type == JointDef.JointType.MotorJoint) {
            joint2 = new MotorJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.MouseJoint) {
            joint2 = new MouseJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.PrismaticJoint) {
            joint2 = new PrismaticJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.PulleyJoint) {
            joint2 = new PulleyJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.RevoluteJoint) {
            joint2 = new RevoluteJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.RopeJoint) {
            joint2 = new RopeJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.WeldJoint) {
            joint2 = new WeldJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.WheelJoint) {
            joint2 = new WheelJoint(this, jointAddr);
        }
        if (joint2 != null) {
            this.joints.put(joint2.addr, joint2);
        }
        JointEdge jointEdgeA = new JointEdge(def.bodyB, joint2);
        JointEdge jointEdgeB = new JointEdge(def.bodyA, joint2);
        joint2.jointEdgeA = jointEdgeA;
        joint2.jointEdgeB = jointEdgeB;
        def.bodyA.joints.add(jointEdgeA);
        def.bodyB.joints.add(jointEdgeB);
        return joint2;
    }

    private long createProperJoint(JointDef def) {
        JointDef jointDef = def;
        if (jointDef.type == JointDef.JointType.DistanceJoint) {
            DistanceJointDef d = (DistanceJointDef) jointDef;
            long j = this.addr;
            long j2 = d.bodyA.addr;
            long j3 = d.bodyB.addr;
            boolean z = d.collideConnected;
            float f = d.localAnchorA.x;
            float f2 = d.localAnchorA.y;
            float f3 = d.localAnchorB.x;
            float f4 = d.localAnchorB.y;
            float f5 = d.length;
            float f6 = d.frequencyHz;
            DistanceJointDef distanceJointDef = d;
            JointDef jointDef2 = def;
            return jniCreateDistanceJoint(j, j2, j3, z, f, f2, f3, f4, f5, f6, d.dampingRatio);
        }
        JointDef jointDef3 = jointDef;
        if (jointDef3.type == JointDef.JointType.FrictionJoint) {
            FrictionJointDef d2 = (FrictionJointDef) jointDef3;
            long j4 = this.addr;
            long j5 = d2.bodyA.addr;
            long j6 = d2.bodyB.addr;
            boolean z2 = d2.collideConnected;
            float f7 = d2.localAnchorA.x;
            float f8 = d2.localAnchorA.y;
            float f9 = d2.localAnchorB.x;
            float f10 = d2.localAnchorB.y;
            float f11 = d2.maxForce;
            float f12 = d2.maxTorque;
            FrictionJointDef frictionJointDef = d2;
            JointDef jointDef4 = jointDef3;
            return jniCreateFrictionJoint(j4, j5, j6, z2, f7, f8, f9, f10, f11, f12);
        }
        JointDef jointDef5 = jointDef3;
        if (jointDef5.type == JointDef.JointType.GearJoint) {
            GearJointDef d3 = (GearJointDef) jointDef5;
            return jniCreateGearJoint(this.addr, d3.bodyA.addr, d3.bodyB.addr, d3.collideConnected, d3.joint1.addr, d3.joint2.addr, d3.ratio);
        } else if (jointDef5.type == JointDef.JointType.MotorJoint) {
            MotorJointDef d4 = (MotorJointDef) jointDef5;
            MotorJointDef motorJointDef = d4;
            return jniCreateMotorJoint(this.addr, d4.bodyA.addr, d4.bodyB.addr, d4.collideConnected, d4.linearOffset.x, d4.linearOffset.y, d4.angularOffset, d4.maxForce, d4.maxTorque, d4.correctionFactor);
        } else if (jointDef5.type == JointDef.JointType.MouseJoint) {
            MouseJointDef d5 = (MouseJointDef) jointDef5;
            return jniCreateMouseJoint(this.addr, d5.bodyA.addr, d5.bodyB.addr, d5.collideConnected, d5.target.x, d5.target.y, d5.maxForce, d5.frequencyHz, d5.dampingRatio);
        } else if (jointDef5.type == JointDef.JointType.PrismaticJoint) {
            PrismaticJointDef d6 = (PrismaticJointDef) jointDef5;
            long j7 = this.addr;
            PrismaticJointDef prismaticJointDef = d6;
            return jniCreatePrismaticJoint(j7, d6.bodyA.addr, d6.bodyB.addr, d6.collideConnected, d6.localAnchorA.x, d6.localAnchorA.y, d6.localAnchorB.x, d6.localAnchorB.y, d6.localAxisA.x, d6.localAxisA.y, d6.referenceAngle, d6.enableLimit, d6.lowerTranslation, d6.upperTranslation, d6.enableMotor, d6.maxMotorForce, d6.motorSpeed);
        } else {
            JointDef jointDef6 = def;
            if (jointDef6.type == JointDef.JointType.PulleyJoint) {
                PulleyJointDef d7 = (PulleyJointDef) jointDef6;
                long j8 = this.addr;
                long j9 = j8;
                PulleyJointDef d8 = d7;
                JointDef jointDef7 = def;
                long j10 = j9;
                PulleyJointDef pulleyJointDef = d8;
                return jniCreatePulleyJoint(j10, d7.bodyA.addr, d7.bodyB.addr, d7.collideConnected, d7.groundAnchorA.x, d7.groundAnchorA.y, d7.groundAnchorB.x, d7.groundAnchorB.y, d7.localAnchorA.x, d7.localAnchorA.y, d7.localAnchorB.x, d7.localAnchorB.y, d8.lengthA, d8.lengthB, d8.ratio);
            }
            JointDef jointDef8 = def;
            if (jointDef8.type == JointDef.JointType.RevoluteJoint) {
                RevoluteJointDef d9 = (RevoluteJointDef) jointDef8;
                long j11 = this.addr;
                long j12 = j11;
                RevoluteJointDef d10 = d9;
                JointDef jointDef9 = def;
                long j13 = j12;
                RevoluteJointDef revoluteJointDef = d10;
                return jniCreateRevoluteJoint(j13, d9.bodyA.addr, d9.bodyB.addr, d9.collideConnected, d9.localAnchorA.x, d9.localAnchorA.y, d9.localAnchorB.x, d9.localAnchorB.y, d9.referenceAngle, d9.enableLimit, d9.lowerAngle, d9.upperAngle, d10.enableMotor, d10.motorSpeed, d10.maxMotorTorque);
            }
            JointDef jointDef10 = def;
            if (jointDef10.type == JointDef.JointType.RopeJoint) {
                RopeJointDef d11 = (RopeJointDef) jointDef10;
                return jniCreateRopeJoint(this.addr, d11.bodyA.addr, d11.bodyB.addr, d11.collideConnected, d11.localAnchorA.x, d11.localAnchorA.y, d11.localAnchorB.x, d11.localAnchorB.y, d11.maxLength);
            }
            if (jointDef10.type == JointDef.JointType.WeldJoint) {
                WeldJointDef d12 = (WeldJointDef) jointDef10;
                long j14 = this.addr;
                long j15 = d12.bodyA.addr;
                long j16 = d12.bodyB.addr;
                boolean z3 = d12.collideConnected;
                float f13 = d12.localAnchorA.x;
                float f14 = d12.localAnchorA.y;
                float f15 = d12.localAnchorB.x;
                float f16 = d12.localAnchorB.y;
                float f17 = d12.referenceAngle;
                float f18 = d12.frequencyHz;
                WeldJointDef weldJointDef = d12;
                return jniCreateWeldJoint(j14, j15, j16, z3, f13, f14, f15, f16, f17, f18, d12.dampingRatio);
            } else if (jointDef10.type != JointDef.JointType.WheelJoint) {
                return 0;
            } else {
                WheelJointDef d13 = (WheelJointDef) jointDef10;
                long j17 = this.addr;
                WheelJointDef wheelJointDef = d13;
                return jniCreateWheelJoint(j17, d13.bodyA.addr, d13.bodyB.addr, d13.collideConnected, d13.localAnchorA.x, d13.localAnchorA.y, d13.localAnchorB.x, d13.localAnchorB.y, d13.localAxisA.x, d13.localAxisA.y, d13.enableMotor, d13.maxMotorTorque, d13.motorSpeed, d13.frequencyHz, d13.dampingRatio);
            }
        }
    }

    public void destroyJoint(Joint joint) {
        joint.setUserData((Object) null);
        this.joints.remove(joint.addr);
        joint.jointEdgeA.other.joints.removeValue(joint.jointEdgeB, true);
        joint.jointEdgeB.other.joints.removeValue(joint.jointEdgeA, true);
        jniDestroyJoint(this.addr, joint.addr);
    }

    public void step(float timeStep, int velocityIterations, int positionIterations) {
        jniStep(this.addr, timeStep, velocityIterations, positionIterations);
    }

    public void clearForces() {
        jniClearForces(this.addr);
    }

    public void setWarmStarting(boolean flag) {
        jniSetWarmStarting(this.addr, flag);
    }

    public void setContinuousPhysics(boolean flag) {
        jniSetContiousPhysics(this.addr, flag);
    }

    public int getProxyCount() {
        return jniGetProxyCount(this.addr);
    }

    public int getBodyCount() {
        return jniGetBodyCount(this.addr);
    }

    public int getFixtureCount() {
        return this.fixtures.size;
    }

    public int getJointCount() {
        return jniGetJointcount(this.addr);
    }

    public int getContactCount() {
        return jniGetContactCount(this.addr);
    }

    public void setGravity(Vector2 gravity2) {
        jniSetGravity(this.addr, gravity2.x, gravity2.y);
    }

    public Vector2 getGravity() {
        jniGetGravity(this.addr, this.tmpGravity);
        Vector2 vector2 = this.gravity;
        float[] fArr = this.tmpGravity;
        vector2.x = fArr[0];
        vector2.y = fArr[1];
        return vector2;
    }

    public boolean isLocked() {
        return jniIsLocked(this.addr);
    }

    public void setAutoClearForces(boolean flag) {
        jniSetAutoClearForces(this.addr, flag);
    }

    public boolean getAutoClearForces() {
        return jniGetAutoClearForces(this.addr);
    }

    public void QueryAABB(QueryCallback callback, float lowerX, float lowerY, float upperX, float upperY) {
        this.queryCallback = callback;
        jniQueryAABB(this.addr, lowerX, lowerY, upperX, upperY);
    }

    public Array<Contact> getContactList() {
        int numContacts = getContactCount();
        if (numContacts > this.contactAddrs.length) {
            int newSize = numContacts * 2;
            this.contactAddrs = new long[newSize];
            this.contacts.ensureCapacity(newSize);
            this.freeContacts.ensureCapacity(newSize);
        }
        if (numContacts > this.freeContacts.size) {
            int freeConts = this.freeContacts.size;
            for (int i = 0; i < numContacts - freeConts; i++) {
                this.freeContacts.add(new Contact(this, 0));
            }
        }
        jniGetContactList(this.addr, this.contactAddrs);
        this.contacts.clear();
        for (int i2 = 0; i2 < numContacts; i2++) {
            Contact contact2 = this.freeContacts.get(i2);
            contact2.addr = this.contactAddrs[i2];
            this.contacts.add(contact2);
        }
        return this.contacts;
    }

    public void getBodies(Array<Body> bodies2) {
        bodies2.clear();
        bodies2.ensureCapacity(this.bodies.size);
        Iterator<Body> iter = this.bodies.values();
        while (iter.hasNext()) {
            bodies2.add(iter.next());
        }
    }

    public void getFixtures(Array<Fixture> fixtures2) {
        fixtures2.clear();
        fixtures2.ensureCapacity(this.fixtures.size);
        Iterator<Fixture> iter = this.fixtures.values();
        while (iter.hasNext()) {
            fixtures2.add(iter.next());
        }
    }

    public void getJoints(Array<Joint> joints2) {
        joints2.clear();
        joints2.ensureCapacity(this.joints.size);
        Iterator<Joint> iter = this.joints.values();
        while (iter.hasNext()) {
            joints2.add(iter.next());
        }
    }

    public void dispose() {
        jniDispose(this.addr);
    }

    private boolean contactFilter(long fixtureA, long fixtureB) {
        ContactFilter contactFilter2 = this.contactFilter;
        if (contactFilter2 != null) {
            return contactFilter2.shouldCollide(this.fixtures.get(fixtureA), this.fixtures.get(fixtureB));
        }
        Filter filterA = this.fixtures.get(fixtureA).getFilterData();
        Filter filterB = this.fixtures.get(fixtureB).getFilterData();
        boolean collide = true;
        if (filterA.groupIndex != filterB.groupIndex || filterA.groupIndex == 0) {
            if ((filterA.maskBits & filterB.categoryBits) == 0 || (filterA.categoryBits & filterB.maskBits) == 0) {
                collide = false;
            }
            return collide;
        } else if (filterA.groupIndex > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void beginContact(long contactAddr) {
        Contact contact2 = this.contact;
        contact2.addr = contactAddr;
        ContactListener contactListener2 = this.contactListener;
        if (contactListener2 != null) {
            contactListener2.beginContact(contact2);
        }
    }

    private void endContact(long contactAddr) {
        Contact contact2 = this.contact;
        contact2.addr = contactAddr;
        ContactListener contactListener2 = this.contactListener;
        if (contactListener2 != null) {
            contactListener2.endContact(contact2);
        }
    }

    private void preSolve(long contactAddr, long manifoldAddr) {
        Contact contact2 = this.contact;
        contact2.addr = contactAddr;
        Manifold manifold2 = this.manifold;
        manifold2.addr = manifoldAddr;
        ContactListener contactListener2 = this.contactListener;
        if (contactListener2 != null) {
            contactListener2.preSolve(contact2, manifold2);
        }
    }

    private void postSolve(long contactAddr, long impulseAddr) {
        Contact contact2 = this.contact;
        contact2.addr = contactAddr;
        ContactImpulse contactImpulse = this.impulse;
        contactImpulse.addr = impulseAddr;
        ContactListener contactListener2 = this.contactListener;
        if (contactListener2 != null) {
            contactListener2.postSolve(contact2, contactImpulse);
        }
    }

    private boolean reportFixture(long addr2) {
        QueryCallback queryCallback2 = this.queryCallback;
        if (queryCallback2 != null) {
            return queryCallback2.reportFixture(this.fixtures.get(addr2));
        }
        return false;
    }

    public void rayCast(RayCastCallback callback, Vector2 point1, Vector2 point2) {
        rayCast(callback, point1.x, point1.y, point2.x, point2.y);
    }

    public void rayCast(RayCastCallback callback, float point1X, float point1Y, float point2X, float point2Y) {
        this.rayCastCallback = callback;
        jniRayCast(this.addr, point1X, point1Y, point2X, point2Y);
    }

    private float reportRayFixture(long addr2, float pX, float pY, float nX, float nY, float fraction) {
        RayCastCallback rayCastCallback2 = this.rayCastCallback;
        if (rayCastCallback2 == null) {
            return 0.0f;
        }
        Vector2 vector2 = this.rayPoint;
        vector2.x = pX;
        vector2.y = pY;
        Vector2 vector22 = this.rayNormal;
        vector22.x = nX;
        vector22.y = nY;
        return rayCastCallback2.reportRayFixture(this.fixtures.get(addr2), this.rayPoint, this.rayNormal, fraction);
    }
}
