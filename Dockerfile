FROM openjdk:17
WORKDIR /app
COPY /target/blogpost-0.0.1-SNAPSHOT.jar /app/PerfectFashionBlogApi-app.jar
EXPOSE 8080
CMD ["java", "-jar", "PerfectFashionBlogApi-app.jar"]