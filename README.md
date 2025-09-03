# **Reminder Project**
### Version 0.2

## Personal Budget Tool

---

## **Goal**
The aim of this project is to remove rust and regain personal dev skills.  
It will be built in stages:

1. Basic monolith app  
2. Add security and authentication (JWT)  
3. Polish and refinement  

---

## **Feature Scope (V 0.2)**

### Admin Panel – User Management

- ✅ View all users
- 
- ✅ Search user profile (by given name, surname, service level)
- ✅ View user by ID
- ✅ Edit user profile
- ✅ Activate/deactivate user accounts
- ✅ Create special users (e.g., admins, apps)
- ✅ Delete user accounts
- ✅ Reset password on behalf of user

### Self-Service (User-Side)

- ✅ User registration (`/auth`)
- ✅ User login (`/auth/login`)
- ✅ Activate user account (`/auth/activate`)
- ✅ View own profile (`/self_service`)
- ✅ Edit own profile (`/self_service` - PATCH)
- ✅ Change password (`/self_service/reset_password`)
- ✅ Delete account (`/self_service` - DELETE)

---

## **Tech Stack**

### **Backend**
- **Java** – Core programming language for backend services.
- **Spring Boot** – For building the monolith backend.
- **Spring Security** – For managing user authentication and session handling.
- **JPA/Hibernate** – For ORM and database interactions.

### **Frontend**
- **Vanilla JavaScript** – For implementing dynamic features and asynchronous operations.
- **Bootstrap** – For styling and responsive design.

### **Database**
- **MySQL** or **MariaDB** – For production database.  
- **H2** – For local development.

---

## **Architecture Overview**
_Describe the overall architecture (controllers, services, security layers, etc.)_

---

## **API Endpoints**
_Document key endpoints, request/response structures, expected status codes, etc._

---

## **Authentication & Authorization**
_Explain JWT setup, roles, secured endpoints, etc._

---

## **Setup & Installation**
_How to run locally (DB setup, configs, etc.)_

---

## **Development Notes**
_Things you learned or had to explore during the project_

---

## **Future Enhancements**
- User self-service UI
- Application-type users (app tokens)
- UI framework (React? Vue?)
- CI/CD and deployment automation
- Testing coverage expansion

---

### 📌 This README file will be updated on the go.