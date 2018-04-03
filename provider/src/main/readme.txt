java -jar eureka-provider-0.0.1-SNAPSHOT.jar --server.port=8080  
java -jar eureka-provider-0.0.1-SNAPSHOT.jar --server.port=8081

java -jar provider-0.0.1-SNAPSHOT.jar --server.port=8080  
java -jar provider-0.0.1-SNAPSHOT.jar --server.port=8081

java -jar provider-0.0.1-SNAPSHOT.jar --spring.profiles.active=p1  
java -jar provider-0.0.1-SNAPSHOT.jar --spring.profiles.active=p2

java -jar eureka-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer1  
java -jar eureka-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer2