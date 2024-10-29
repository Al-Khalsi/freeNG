[TOC]

# Prompt

I was thinking of creating a web application using react and spring boot.

I was thinking of using AWS as my host provider (and other AWS services).

My Idea is: "A Free PNG downloading website. We (me and my friend) will upload PNG pictures for the first few months, and when we get to earning money (google Ad sense), we'll hire an admin to continue uploading PNG pictures everyday."

My questions:

1. Overall, what do you think about my idea? Is it worth putting the effort? Any disadvantages? Any notes? Suggestions?
2. Which cloud provider do you think is suitable for this project? Should I go for AWS or should I just use digital ocean, or other service providers?
3. If I use AWS; I was thinking of using these services:
   - S3: for storing the images.
   - CloudFront: for downloading with low latency.
   - `Elastic Bean Stalk` or `EC2` or `AWS Lambda`: for the host. I'm quite confused in choosing this one. Since this is a new project, I was thinking of using AWS Lambda, since it won't charge me for no user coming to my website, right? But, I don't know if it'll be able to handle a huge amount of requests in the future if the users do come to my website for downloading.
   - `ECS`, `ECR`, `AWS Codebuild`, `AWS Codepipeline`: if I decide to write my code based on dockerizing and using CI/CD. (For this part I'm a bit skeptic and indecisive. Should I go for CI/CD? Is my project even going to scale up that much? Or it'll be enough in 2 or 3 versions? Since I'm only thinking of making this project just until it gets validated by google ad sense and then giving it to the admin).



# Result

1. **S3 (Simple Storage Service):**

   **Use case:** Storing images and static content.

   **Why choose it:** Highly scalable, reliable, and cost-effective object storage service. Ideal for storing large amounts of data.

2. **DynamoDB:**

   **Use case:** Storing metadata associated with images (e.g., title, description, tags).

   **Why choose it:** NoSQL database that offers flexible schema, high performance, and scalability. Suitable for real-time data access and unpredictable workloads.

3. **AWS Lambda:**

   **Use case:** Serverless compute platform for running backend logic.

   **Why choose it:** Cost-effective, scalable, and serverless, eliminating the need to manage servers. Ideal for handling event-driven workloads and API requests.

4. **API Gateway:**

   **Use case:** Creating and managing RESTful APIs for your frontend application.

   **Why choose it:** Provides a managed API layer, handles authentication, authorization, and rate limiting. Simplifies API development and management.

5. **CloudFront:**

   **Use case:** Content delivery network (CDN) for delivering images with low latency.

   **Why choose it:** Improves performance by caching content closer to users, reducing load on your origin servers.

6. **Route 53:**

   **Use case:** Domain name system (DNS) service for managing domain names and DNS records.

   **Why choose it:** Offers high availability, scalability, and integration with other AWS services. Simplifies domain name management.

7. **Amplify (Optional):**

   **Use case:** Full-stack development platform for building and deploying frontend and backend applications.

   **Why choose it:** Provides a managed hosting environment for your React app, along with additional features like authentication and data synchronization. Can simplify development and deployment.

8. **CodeBuild (Optional):**

   **Use case:** Fully managed continuous integration service for building your application code.

   **Why choose it:** Automatically builds your code into Docker images, eliminating manual build processes.

9. **CodePipeline (Optional):**

   **Use case:** Continuous delivery service for automating the release pipeline.

   **Why choose it:** Orchestrates the entire CI/CD process, from code changes to deployment.

10. **ECS (Elastic Container Service) (Optional):**

    **Use case:** Container orchestration service for managing and scaling your Docker containers.

    **Why choose it:** Provides a scalable and efficient environment for running your Lambda functions or other containerized applications.

11. **ECR (Elastic Container Registry) (Optional):**

    **Use case:** Secure registry for storing Docker images.

    **Why choose it:** Ensures your Docker images are readily available for deployment.