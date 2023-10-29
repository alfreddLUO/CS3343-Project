# GoGo Eat
GoGo Eat is a food court management system targeting customers, merchants, and admins. 

The food-court-like restaurant contains several dining windows that customers can order food from, and an open space where customer can sit-in to dine on a first-come-first-served basis or through reservation.

* Functionalities
    * Customers 
        * Dine in
        * Reserve a table
        * Queue
        * Order food
        * Online Payment 
    * Merchants 
        * Manage dishes
        * Edit dish prices and names
        * Assist in payments
    * Admins
        * Change the opening hours of the food court
        * Check Customers' orders
        * Check Customers' Reservations
        * Edit Restaurants
        * Edit Tables

# About Folders
There are three folders for the submission, they are Docs, Release, and Source.

## 1. Docs
The Docs folder contains the following documentations:
* Project Plan
* Design and Analysis Report
* Test Report
* Bug Report
* Self-Reflection Report

## 2. Release
The Release folder contains the java executable files of the program,namely `GoGoEat-exe.jar`.

## 3. Source
The Source Folder contains a sub-folder named src, inside src folder, there are another two sub-folders that are `GoGoEat`, and `TestGoGoEat`.

The `GoGoEat` folder contains all developing java files, and the `TestGoGoEat` folder contains all testing files.

## 4. Appendix
Supplementary Document, e.g. ClassDiagram.pdf is located here for reference.

# Installation

## 1. Run program (java executable)
The java executable file `GoGoEat-exe.jar` is located in the `Release` folder.

Execute the following command in terminal: ` java -jar GoGoEat-exe.jar `

## 2. Run program (source code)
Package of library required (The jar file of the package is included in the `Source` Folder):

* Download from `https://search.maven.org/search?q=g:com.google.guava%20AND%20a:guava`
*  Add the downloaded jar file into java project library
* Run from main

# User Guideline

## 1. Run as Admin
The account for admin permission has already been set up.

* Username: admin
* Password: t123

## 2. Run as Merchant
User can register a merchant account through the Registration function.

For aid, the existing account is also provided for testing:
* Username: KFCWorker
* Password: t123

## 3. Run as Customer
User can register a customer account through the Registration function.

For aid, the existing account is also provided for testing:
* Username: yinch33
* Password: t123