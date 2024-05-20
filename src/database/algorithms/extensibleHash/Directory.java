package database.algorithms.extensibleHash;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import database.domain.persistence.Serializable;

public class Directory implements Serializable {
    protected byte globalDeep;
    protected long[] addresses;

    public Directory(){
        globalDeep = 0;
        addresses = new long[1];
        addresses[0] = 0;
    }

    public Directory(byte[] buffer) throws IOException{
        this();
        this.fromByteArray(buffer);
    }

    protected int hash(int chave) {
        return hash(chave, this.globalDeep);
    }

    protected int hash(int chave, int localDeep) {
        return Math.abs(chave) % (int) Math.pow(2, localDeep);
    }

    public long getBucketOffset(int index){
        if (index > Math.pow(2, globalDeep))
            return -1;
        return addresses[index];
    }

    protected boolean duplicate(){
        if(globalDeep >= Byte.MAX_VALUE)
            return false;

        int firstHalf = (int)Math.pow(2, globalDeep);
        int secondHalf = firstHalf * 2;

        long[] currAdresses = new long[secondHalf];

        for(int i = 0; i < firstHalf; i++){
            currAdresses[i] = this.addresses[i];
        }
        for(int i = firstHalf; i < secondHalf; i++){
            currAdresses[i] = this.addresses[i - firstHalf];
        }
        this.globalDeep++;
        this.addresses = currAdresses;
        return true;
    }

    protected boolean updateAddress(int index, long updatedAddress){
        if(index > Math.pow(2, globalDeep))
            return false;
        addresses[index] = updatedAddress;
        return true;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        var baos = new ByteArrayOutputStream();
        var out = new DataOutputStream(baos);
        out.writeByte(globalDeep);
        int numOfAddress = (int) Math.pow(2, globalDeep);
        for (int i = 0; i < numOfAddress; i++) {
            out.writeLong(addresses[i]);
        }
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] buffer) throws IOException {
        var bais = new ByteArrayInputStream(buffer);
        var in = new DataInputStream(bais);
        globalDeep = in.readByte();
        int numOfAddress = (int) Math.pow(2, globalDeep);
        addresses = new long[numOfAddress];
        for (int i = 0; i < numOfAddress; i++) {
            addresses[i] = in.readLong();
        }
    }

    @Override
    public int getByteLength() {
        return Byte.BYTES + (Long.BYTES * addresses.length);
    }

}
