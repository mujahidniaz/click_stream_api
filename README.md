# Click/Impression Stream API

## Technologies:

•	Scala

•	Dockers Containers

•	Redis (as Cache)

•	MySQL (as Storage)

•	Scalatera (Rest Server End point)

•	Jetty Server (Web Server)


## How Did I approach This Problem?

I started the design with very basic approach first and then by revising improved it with for more scalability and performance

### First Approach (very basic):

In my first approach towards the solution I evaluated few archtitecture and storage solutions. For example NO SQL and SQL solutions. Created a simple API and Back-end with MySQL. Everytime a POST call is received data is pushed to My SQL table. And on every Get call this data is quried from the table. But this approach was bad because if we are talking about Millions of requests per day. As the data grows bigger queriying the table becomes slower and the aggregate functions like `count(*)` become even slower. So how to avoid querying data on every call? that was my next approach.

### Second Approach (a bit better):
In my second approach my goal was to avoid the aggregations on millions of data on every call so it was better to keep the data aggregated in the storage instread of aggregating it on every call repeatedly. So in this approach another table was created in which all the data was kept in aggregated form e.g count of impressions, count of clicks and number of unique users for each hour. Data was partitioned on the basis of hour so that access was faster and each query was qurying a partition of data so it was not a full table scan. But it was still a bad design because we are still qurying MySQL on each call.

### Third Approach (much better):

In this approach, I introduced `Redis` as a `Cache`. `Redis` is a key value `In Memory` NO-SQL like database. It is much much faster than MySQL. So goal was to keep the aggregated data in Redis cache. But in Memory database can take a lot of memory if we keep a lot of data on it so retention period was introduced. I kept 2 hours of data in the cache so that our 95% of request get the highest speed (lowest latency) for current hour and others can be quried from MySQL. This case is really better than the previous one. Our 95% requests have the current hour timestamp so we can deal with them instantly using in Memory. But I wanted to improve it further.

### Fourth Approach (Final & best one so far):
In this approach I wanted to have something like a **`Multi Level Hashing`** so that i can have a linear time complexity. And fortunately Redis gives you really nice multi Hashing technique and it suits perfectly on this case. In this approached I introduced **`Redis HyperLogLog`** 

**What is Hyperloglog?**
A HyperLogLog is a probabilistic data structure used to count unique values — or as it’s referred to in mathematics: calculating the cardinality of a set.These values can be anything: for example, IP addresses for the visitors of a website, search terms, or email addresses.
Counting unique values with exact precision requires an amount of memory proportional to the number of unique values. The reason for this is that there is no way of determining if a value has already been seen other than by comparing it to the previously seen values.
Since memory is a limited resource, doing this becomes problematic when working with large sets of values.

A HyperLogLog solves this problem by allowing to trade memory consumption for precision making it possible to estimate cardinalities larger than 109 with a standard error of 2% using only 1.5 kilobytes of memory

So in this case I can keep aggregated data for 24 Hours which can handle 99% of the requests and in case of 1% (maybe never) we will have to query MySQL. and Each key takes up to **`12 MB`** of data. So in this case 3 keys (impressions, Users, Clicks) **`3*12=36 MB`** for an hour. **`36*24=864 MB`** can handle 24 Hours of requests in Memory with lighting speed due to Multi level Hashing and each key (Hash) will avoid the extra calculations.

### Further Improvements that I can do if I get more time:
Further improvements would be to use **`Scala Play framework`** for Async API Calls which will work much more faster but that can complicate the solution so i kept it for future developments.

For Async calls we will have to keep a messaging Queue as well we can use **`Apache Kafka, ActiveMQ or RabitMQ`** so that MySQL Queries can be queued and executed according to ACID principle. Otherwise MySQL might get choked due to multiple conqurent writes.

This was my first time using scala for Web Development / API development. I have used scala often for **`Spark`** and Data processing but never used for this purpose. 


## Pre-Requisites:
•	Docker Engine Must be installed
•	Java 1.8 + must be installed and configured.


## How to run the Solution:

•	First open terminal go to Docker Directory in the Solution main folder
•	run **`docker-compose up`** and docker images will setup.
•	Now open the project in Eclipse and Run Server.scala. Server will start listening at port 7777
•	Go to your browser and make request like 
	`GET => http://localhost:7777/analytics?timestamp=1578651952635`
	`POST => http://localhost:7777/analytics?timestamp=1578651952635&user=foo&click`


## Demo (Video):

Kindly watch https://www.youtube.com/watch?v=ReCpv6FhC-I&feature=youtu.be this youtube video for Demo and how to get it Started.