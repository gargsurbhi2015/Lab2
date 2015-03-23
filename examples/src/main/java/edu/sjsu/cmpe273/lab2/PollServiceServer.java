package edu.sjsu.cmpe273.lab2;

import io.grpc.ServerImpl;
import io.grpc.stub.StreamObserver;
import io.grpc.transport.netty.NettyServerBuilder;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;
import java.util.logging.Logger;
import java.util.logging.Level;


/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class PollServiceServer {
  private static final Logger logger = Logger.getLogger(PollServiceServer.class.getName());

    

    /* Server port information */
    private int port = 50051;
    private ServerImpl server;

    private void start() throws Exception {
        server = NettyServerBuilder.forPort(port)
                .addService(PollServiceGrpc.bindService(new PollServiceImpl()))
                .build().start();
        logger.info("Server started, listening on : " + port );
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                PollServiceServer.this.stop();
                System.err.println("*** server shut down");
            }
        });

    }

    private void stop(){
        if(server != null){
            server.shutdown();
        }
    }

    /* Main method will launch the server */
    public static void main(String[] args) throws Exception{
        final PollServiceServer server = new PollServiceServer();
        server.start();
    }

    private class PollServiceImpl implements PollServiceGrpc.PollService{
        @Override
        public void createPoll(PollRequest req, StreamObserver<PollResponse> responseObserver){
try{
            if(req.getChoiceList()!=null){

                if(req.getChoiceList().size()<2)
                    throw new RuntimeException("Choice List is less than two");

            }else{
                throw new RuntimeException("Choice is not given");
            }



        String s="{";
    for(int i=0;i<req.getChoiceCount();i++){

        s=s+req.getChoice(i);
        if(i!=req.getChoiceCount()-1)
            s=s+",";


    }
    s=s+"}";
    logger.info("\n****************************\nModerator ID Received from Client is :"+req.getModeratorId()+"\nQuestion Received from Client: "+req.getQuestion()+"\nStart Date Received from Client: "+req.getStartedAt()+"\nEnd Date Received from Client is: "+req.getExpiredAt()+"\nChoice Received from Client: "+s+"\n**************************");
    //       logger.info("\nChoice "+s+"\n***********************");

            final AtomicInteger counter = new AtomicInteger(111111);
            String poll_id = Integer.toHexString(counter.getAndIncrement());
            PollResponse reply = PollResponse.newBuilder().setId(poll_id).build();
            responseObserver.onValue(reply);
            responseObserver.onCompleted();
        }catch(RuntimeException e){
            logger.log(Level.WARNING,"RPC Failed",e.getMessage());
            return;
        }

        }
    }
}