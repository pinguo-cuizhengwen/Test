package us.pinguo.test;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.ShutdownReason;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.clientlibrary.types.Messages;
import com.amazonaws.services.kinesis.model.Record;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;
import java.util.UUID;

/**
 * Created by pinguo on 17/4/25.
 */
public class Consumer {
    static ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
    private static final InitialPositionInStream SAMPLE_APPLICATION_INITIAL_POSITION_IN_STREAM =
            InitialPositionInStream.TRIM_HORIZON;
    private static String SAMPLE_APPLICATION_NAME = "application-name";
    private static String SAMPLE_APPLICATION_STREAM_NAME = "test-nir";

    public static void main(String[] args) {
//work-id --->10.1.7.92:cc499020-cf66-4bec-8696-b920d058083d
        String workerId = null;
        try {
            workerId = InetAddress.getLocalHost().getCanonicalHostName() + ":" + UUID.randomUUID();
            System.out.println("work-id --->" + workerId);
            KinesisClientLibConfiguration kinesisClientLibConfiguration =
                    new KinesisClientLibConfiguration(SAMPLE_APPLICATION_NAME,
                            SAMPLE_APPLICATION_STREAM_NAME,
                            credentialsProvider,
                            workerId);
            kinesisClientLibConfiguration.withInitialPositionInStream(SAMPLE_APPLICATION_INITIAL_POSITION_IN_STREAM);

            IRecordProcessorFactory recordProcessorFactory = new AmazonKinesisApplicationRecordProcessorFactory();
            Worker worker = new Worker(recordProcessorFactory, kinesisClientLibConfiguration);

            System.out.printf("Running %s to process stream %s as worker %s...\n",
                    SAMPLE_APPLICATION_NAME,
                    SAMPLE_APPLICATION_STREAM_NAME,
                    workerId);

            worker.run();
        } catch (Exception t) {
            System.err.println("Caught throwable while processing data.");
            t.printStackTrace();
        }
    }


    public static class AmazonKinesisApplicationRecordProcessorFactory implements IRecordProcessorFactory {
        @Override
        public IRecordProcessor createProcessor() {
            return new AmazonKinesisApplicationSampleRecordProcessor();
        }
    }

    public static class AmazonKinesisApplicationSampleRecordProcessor implements IRecordProcessor {

        private static final Log LOG = LogFactory.getLog(AmazonKinesisApplicationSampleRecordProcessor.class);
        private String kinesisShardId;

        // Backoff and retry settings
        private static final long BACKOFF_TIME_IN_MILLIS = 3000L;
        private static final int NUM_RETRIES = 10;

        // Checkpoint about once a minute
        private static final long CHECKPOINT_INTERVAL_MILLIS = 60000L;
        private long nextCheckpointTimeInMillis;

        private final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

        /**
         * {@inheritDoc}
         */
        @Override
        public void initialize(String shardId) {
            LOG.info("Initializing record processor for shard: " + shardId);
            this.kinesisShardId = shardId;
        }

        @Override
        public void processRecords(List<Record> records, IRecordProcessorCheckpointer checkpointer) {
            for(Record rec : records){
                System.out.println("partitionKey->"+rec.getPartitionKey());
                System.out.println("SequenceNumber->"+rec.getSequenceNumber());
                System.out.println("data->"+rec.getData());
                try {
                    checkpointer.checkpoint();
                } catch (InvalidStateException e) {
                    e.printStackTrace();
                } catch (ShutdownException e) {
                    e.printStackTrace();
                }

            }
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public void shutdown(IRecordProcessorCheckpointer checkpointer, ShutdownReason reason) {
            LOG.info("Shutting down record processor for shard: " + kinesisShardId);
            // Important to checkpoint after reaching end of shard, so we can start processing data from child shards.
            if (reason == ShutdownReason.TERMINATE) {
                try {
                    checkpointer.checkpoint();
                } catch (InvalidStateException e) {
                    e.printStackTrace();
                } catch (ShutdownException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}