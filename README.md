Code for blog post: Running Java Integration Tests against a PostgreSQL Embedded database - https://rm3l.org/jvm-integration-tests-postgresql-embedded-database/

# Motivations

In-memory databases (e.g., H2, HSQLDB, SQLite, ...) are very often used as drop-in replacements when running integration tests.
This is generally in order to make such tests run fast, self-contained and therefore free of side effects.
This technique seems to suffice for simple interactions, but may not really mimic the database used in production.
So even though all your integration tests pass locally against an in-memory database, 
there is actually no guarantee that the code will work once deployed in a production environment 
which makes use of a different database server.

The goal of the blog post is to explore how we can actually have tests that target a local PostgreSQL database,
with the following constraints in mind:
- real local PostgreSQL database server spinned up on demand and programmatically, with the ability to listen on any available port
- guaranty that all individual test methods start in a clean state, especially from the database perspective
- tests must also be as fast as with an in-memory database

See the related blog post for further details: https://rm3l.org/jvm-integration-tests-postgresql-embedded-database/


