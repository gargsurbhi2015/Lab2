package edu.sjsu.cmpe273.lab2;

import io.grpc.ChannelImpl;
import io.grpc.transport.netty.NegotiationType;
import io.grpc.transport.netty.NettyChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */
public class PollServiceClient {
  private static final Logger logger = Logger.getLogger(PollServiceClient.class.getName());



    private final ChannelImpl channel;
    private final PollServiceGrpc.PollServiceBlockingStub blockingStub;

    public PollServiceClient(String host, int port){
        channel=
                NettyChannelBuilder.forAddress(host, port).negotiationType(NegotiationType.PLAINTEXT)
                        .build();
        blockingStub = PollServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException{
        channel.shutdown().awaitTerminated(5,TimeUnit.SECONDS);
    }


    public void poll(){

        try{
            logger.info("Client Started....");
            PollRequest request = PollRequest.newBuilder()
                    .setModeratorId("786556")
                    .setQuestion("What type of Smart Phone do you have?")
                    .setStartedAt("2015-02-23T13:00:00.000Z")
                    .setExpiredAt("2015-02-24T13:00:00.000Z")
                    .addChoice("Android")
                    .addChoice("iPhone")
                    .build();
            PollResponse response = blockingStub.createPoll(request);
            logger.info("\n**************\nMpderator Id sent to Server is: "+request.getModeratorId());
            logger.info("\n*************************\nPoll Id Received from Server is : " + response.getId()+"\n***************************\n");
        }catch(RuntimeException e){
            logger.log(Level.WARNING,"RPC Failed",e);
            return;
        }
    }


    public static void main(String[] args) throws Exception{
        PollServiceClient client = new PollServiceClient("localhost",50051);
        try{
            client.poll();
        }finally {
            client.shutdown();
        }
    }

}
