package database.algorithms.extensibleHash;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

import database.domain.structs.Index;

public class ExtensibleHash<T extends Index> {
    public static final String dataPath = "./src/database/data/hash/";
    private RandomAccessFile directoryFile;
    private RandomAccessFile bucketsFile;
    private short bucketLimit;
    private Directory directory;
    private Constructor<T> constructor;

    public ExtensibleHash(int bucketLimit, Constructor<T> constructor, String bucketFileName,
            String directoryFileName) throws Exception {
        this.bucketLimit = (short) bucketLimit;
        this.constructor = constructor;
        this.bucketsFile = new RandomAccessFile(dataPath + bucketFileName, "rw");
        this.directoryFile = new RandomAccessFile(dataPath + directoryFileName, "rw");
        if (directoryFile.length() == 0 || bucketsFile.length() == 0) {
            initialize();
        }
    }

    private void initialize() throws Exception {
        directory = new Directory();
        directoryFile.write(directory.toByteArray());
        var bucket = new Bucket<>(bucketLimit, constructor);
        directoryFile.write(bucket.toByteArray());
    }

    public T find(int key) throws Exception {
        directory = deserializeDirectory();

        int i = directory.hash(key);
        long bucketOffset = directory.getBucketOffset(i);

        Bucket<T> bucket = deserializeBucket(bucketOffset);
        return bucket.find(key);
    }

    public boolean save(T index) throws Exception {
        directory = deserializeDirectory();
        int hash = directory.hash(index.getId());
        long bucketOffset = directory.getBucketOffset(hash);

        Bucket<T> bucket = deserializeBucket(bucketOffset);
        
        if (bucket.find(hash) != null)
            throw new Exception("Key already persisted");

        if(!bucket.isFull()){
            bucket.save(index);
            serializeBucket(bucketOffset, bucket);
            return true;
        }
        
        if(bucket.localDeep >= directory.globalDeep)
            directory.duplicate();
            
        Bucket<T> b1 = new Bucket<>((byte)(bucket.localDeep + 1), bucketLimit, constructor);
        serializeBucket(bucketOffset, b1);

        long newBucketOffset = bucketsFile.length();

        Bucket<T> b2 = new Bucket<>((byte)(bucket.localDeep + 1), bucketLimit, constructor);
        serializeBucket(newBucketOffset, b2);
        
        int j = directory.hash(index.getId(), bucket.localDeep);
        int interval = (int) Math.pow(2, bucket.localDeep);
        int N = (int) Math.pow(2, directory.globalDeep);
        boolean flag = false;

        while (j < N) {
            if(flag)
                directory.updateAddress(j, newBucketOffset);

            flag = !flag;
            j += interval;
        }

        serializeDirectory();

        for(int i = 0; i < bucket.quantity; i++){
            save(bucket.indexes.get(i));
        }
        save(index);

        return true;
    }

    private void serializeBucket(long bucketOffset, Bucket<T> bucket) throws IOException{
        bucketsFile.seek(bucketOffset);
        bucketsFile.write(bucket.toByteArray());
    }

    private Bucket<T> deserializeBucket(long bucketOffset) throws Exception {
        Bucket<T> bucket = new Bucket<>(bucketLimit, constructor);
        byte[] buffer = new byte[bucket.getByteLength()];
        bucketsFile.seek(bucketOffset);
        bucketsFile.read(buffer);
        bucket.fromByteArray(buffer);
        return bucket;
    }

    private void serializeDirectory() throws IOException{
        directoryFile.seek(0);
        directoryFile.write(directory.toByteArray());
    }

    private Directory deserializeDirectory() throws IOException {
        directoryFile.seek(0);
        byte[] buffer = new byte[(int) directoryFile.length()];
        directoryFile.read(buffer);
        return new Directory(buffer);
    }
}
