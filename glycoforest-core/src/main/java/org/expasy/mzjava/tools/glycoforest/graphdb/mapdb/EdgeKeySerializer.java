package org.expasy.mzjava.tools.glycoforest.graphdb.mapdb;

import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
class EdgeKeySerializer implements Serializer<EdgeKey>, Serializable {

    @Override
    public void serialize(DataOutput out, EdgeKey key) throws IOException {

        if(key == null) {

            //BTree's are not supposed to have null keys, however due to a bug in MapDB it does, this seems to fix it.
            //See https://gist.github.com/mattwigway/230b9589be891a95e7eb
            out.write(1);
            return;
        }

        out.writeFloat(key.getMz());
        out.writeInt(key.getCount());
    }

    @Override
    public EdgeKey deserialize(DataInput in, int available) throws IOException {

        return new EdgeKey(in.readFloat(), in.readInt());
    }

    @Override
    public int fixedSize() {

        return 8;
    }
}
