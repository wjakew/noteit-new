# 

<img src="file:///./readme_resources/icon-dark.png" title="" alt="icon-dark.png" data-align="center">

# NoteIT Web Application.

Development still in progress. Full doc will arrive with GM release.

**Created with:**

- JAVA 20
- Vaadin
- MySQL
- Hibernate

**Application key features:**

- Creating Vaults for storing notes.

- Creating notes using build-in editor (standard or pro)

- Simple server migration.

- Sharing stored Notes.

- Simple Task Managment based on notes.

**Database Architecture:**

![database_schema.png](.\readme_resources\database_schema.png)

**Application Architecture**

![noteit_architecture.png](.\readme_resources\noteit_architecture.png)

**Application Screenshots**

![noteitapplication_screenshot1.png](.\readme_resources\noteitapplication_screenshot1.png)

![noteitapplication_screenshot2.png](.\readme_resources\noteitapplication_screenshot2.png)

![noteitapplication_screenshot3.png](.\readme_resources\noteitapplication_screenshot3.png)

**Deployment Instructions**

1. Install MysqlServer >=8.0.33

2. Unpack the application archive.

3. Run noteit_database_make.sql

4. Run application:
   
   ```bash
   java -jar noteit_application.jar
   ```