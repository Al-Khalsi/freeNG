# Project Deployment Report: Free PNG Downloading Website

[TOC]

## Prompt

I want deploy a spring boot and react application to the world and make it available world wide. The project is: "A free
PNG downloading website. Since it is about downloading, being low-latency is top priority and ease of download and
browsing."

> Assume the project is implemented and done, and we're only stuck in the deployment phase.

I know what will I need and I'll tell it to you. Correct me if I'm wrong or if I miss anything:

- **Server**: For running spring boot and react in it. It has Core, CPU, RAM, Storage (this storage is for the code,
  compilers, dependencies, etc... that comes with the project itself. Right?). I'll buy a server that has an OS
  installed, typically linux base like ubuntu. I'll install JDK, Node.js, MySQL and upload my code files: both react and
  spring in the server and run both of them. Right now, it is not available to world, is running in localhost in the
  server and I can access it by putty and curl.)

    - **Web-Server**: Like Nginx. This will be used for...? for what? why would I need this? in any case, I plan to use
      this in the next version of my web app.

- **Storage:** For storing the PNG files. It is basically GB.

  > I don't know whether to buy a storage service, or a downloading storage service. Since it is a PNG downloading
  website, users will browse and download PNG pictures, so the downloading should be easy and fast.

- **DNS:** A domain name so that when the user hits that domain name, say; `picng.com`, it'll internally hit the IP
  address of my server, where react and spring boot are running and communicating with each other.

- **CDN:** To cache and make the PNG pictures, easily downloaded. To make it closer to people in every point of world.
  For making the website low-latency.

- **SSL Certificate:** For making the website `https` from `http` so that it is secure and encrypted user data.

## Response

### Project Overview

We are developing a free PNG downloading website aimed at providing users with easy access to downloadable PNG images.
Our priority is to ensure low latency for users and an efficient browsing experience.

### Deployment Plan

#### 1. **Server Requirements**

- **Type:** We will choose between a Virtual Private Server (VPS) or a Cloud Server based on budget and scalability
  needs.
- **Operating System:** Ubuntu (Linux-based).
- Software to Install:
    - **Java Development Kit (JDK):** For running the Spring Boot application.
    - **Node.js:** For building and serving the React application.
    - **MySQL:** For our database needs.
    - **Nginx:** As a web server to serve static files and act as a reverse proxy to our Spring Boot backend.

#### 2. **Web Server Setup (Nginx)**

- Nginx will be used to:
    - Serve the React application and static files.
    - Route API requests to the Spring Boot application.
    - Handle SSL termination for secure HTTPS connections.

#### 3. **Storage for PNG Files**

- **Cloud Storage:** We will consider using a cloud storage service (e.g., AWS S3, Google Cloud Storage) to store PNG
  files for scalability and fast access.

#### 4. **Domain Name and DNS**

- We will purchase a domain name (e.g., `picng.com`) and configure DNS settings to point to our server’s IP address.

#### 5. **Content Delivery Network (CDN)**

- A CDN will be integrated to cache PNG images and deliver them with low latency to users worldwide.

#### 6. **SSL Certificate**

- We will obtain an SSL certificate (e.g., from Let’s Encrypt) to secure the website with HTTPS.

### Server Options

#### **1. Virtual Private Server (VPS)**

- Pros:
    - Cost-effective and provides dedicated resources.
    - Full control over the server environment.
    - Scalable as traffic increases.
- Cons:
    - Shared resources with other users, which may affect performance.

#### **2. Cloud Server**

- Pros:
    - Highly scalable and flexible to manage fluctuating traffic.
    - Better redundancy and uptime due to multiple data centers.
    - Pay-as-you-go pricing model.
- Cons:
    - More complex setup and management compared to VPS.
    - Costs can vary based on usage.

#### **3. Dedicated Server**

- Pros:
    - Maximum performance with full access to all resources.
    - Complete control over server configuration.
- Cons:
    - Significantly more expensive.
    - Requires more management effort.

#### Recommendation

- **Best Option:** Start with a **VPS** for cost-effectiveness and ease of management. As the project grows, we can
  consider transitioning to a Cloud Server if necessary.

### Key Considerations

1. **Security:** Regularly update software, configure firewalls, and monitor for unauthorized access.
2. **Backup Strategy:** Implement regular backups of the database and PNG files.
3. **Performance Monitoring:** Use tools to monitor server performance and application health.
4. **Documentation:** Keep detailed documentation of the setup process for future reference.

### Next Steps

1. Choose a cloud provider and set up the server.
2. Install the required software and deploy the applications.
3. Configure Nginx and set up the domain name and SSL certificate.
4. Integrate cloud storage and CDN for PNG files.

# Resource Estimation

## Prompt

can you make an estimate on how much resource I'd need for my server to install and run these softwares, in order to run
react frontend and spring boot backend:

- docker (for PostgreSQL so will pull a db image as well)
- jdk
- maven
- node.js
- nginx or caddy

## Response

1. Aval AI

   > ### 1. **Component Resource Requirements**
   >
   > #### **Docker**
   >
   > - **CPU**: Minimal overhead, but depends on the containers you run.
   > - **Memory**: Approximately 512 MB to 1 GB for the Docker daemon itself.
   >
   > #### **PostgreSQL (Docker Container)**
   >
   > - **CPU**: 1 vCPU (more for high-load scenarios).
   > - **Memory**: 1 GB minimum; 2 GB recommended for better performance.
   > - **Storage**: At least 10 GB for the database, depending on your data size and growth.
   >
   > #### **Java Development Kit (JDK)**
   >
   > - **CPU**: Minimal overhead.
   > - **Memory**: 512 MB to 1 GB should suffice for running applications, but development might require more.
   >
   > #### **Maven**
   >
   > - **CPU**: Minimal overhead.
   > - **Memory**: 512 MB is usually sufficient.
   >
   > #### **Node.js (for React)**
   >
   > - **CPU**: 1 vCPU.
   > - **Memory**: 1 GB minimum; 2 GB recommended for development and building applications.
   >
   > #### **Nginx or Caddy**
   >
   > - **CPU**: 1 vCPU.
   > - **Memory**: 256 MB to 512 MB is typically sufficient for serving static files and reverse proxying.
   >
   > ### 2. **Overall Resource Estimate**
   >
   > Based on the above components, here’s a consolidated estimate for running everything smoothly:
   >
   > #### **CPU**
   >
   > - Total vCPUs
       >
       >   :
       >
       >
    - Docker: Minimal
   > - PostgreSQL: 1
   >   - JDK: Minimal
   >   - Maven: Minimal
   >   - Node.js: 1
   >   - Nginx/Caddy: 1
   >
   > - **Estimated Total**: 3 vCPUs (considering the load)
   >
   > #### **Memory**
   >
   > - Total Memory
       >
       >   :
       >
       >
    - Docker: 1 GB
   > - PostgreSQL: 2 GB
   >   - JDK: 1 GB
   >   - Maven: 0.5 GB
   >   - Node.js: 2 GB
   >   - Nginx/Caddy: 0.5 GB
   >
   > - **Estimated Total**: 7 GB (to allow for some breathing room, especially under load)
   >
   > #### **Storage**
   >
   > - Total Storage
       >
       >   :
       >
       >
    - PostgreSQL: 10 GB minimum
   > - Additional space for your application code, Docker images, and logs: 10-20 GB
   >
   > - **Estimated Total**: 30 GB (considering growth and additional files)
   >
   > ### 3. **Recommended Server Specifications**
   >
   > Based on the estimates, here’s a recommended server configuration:
   >
   > - **CPU**: 4 vCPUs
   > - **Memory**: 8 GB RAM
   > - **Storage**: 40 GB SSD (to allow for performance and growth)
   >
   > ### 4. **Considerations**
   >
   > - **Scaling**: If you expect high traffic or significant data growth, consider scaling resources accordingly or
       using cloud services that allow for easy scaling.
   > - **Environment**: The requirements may vary based on whether you are in a development, testing, or production
       environment.
   > - **Monitoring**: Implement monitoring tools to track resource usage and optimize as needed.
   >
   > ### Conclusion
   >
   > For a development or small production environment, a server with 4 vCPUs, 8 GB of RAM, and 40 GB of SSD storage
   should be sufficient to run Docker, PostgreSQL, JDK, Maven, Node.js, and either Nginx or Caddy effectively. Adjust
   these estimates based on your specific use case, expected load, and growth projections.

# References

#- [Dreams of Code - Setting Up a Production Ready VPS is a lot easier than you think](unstuckstudy.com/share/ce8e7507-054f-42a6-8a17-30058e470836)