DROP DATABASE IF EXISTS `01-orchitech-todo`;
CREATE DATABASE `01-orchitech-todo`;
USE `01-orchitech-todo`;

DROP TABLE IF EXISTS `todoEntry`;

CREATE TABLE `todoEntry` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT NOT NULL,
  `status` ENUM('CREATED', 'IN_PROCESS', 'FINISHED', 'FAILED') NOT NULL,
  `createdAt` DATE NOT NULL,
  `deadline` DATE NOT NULL,
  `completedAt` DATE NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

INSERT INTO `todoEntry` (`title`, `description`, `status`, `createdAt`, `deadline`, `completedAt`) VALUES
('Implement login feature', 'Create Spring MVC controller and service for user login', 'CREATED', '2025-07-30', '2025-08-07', NULL),
('Refactor database layer', 'Clean up JPA entities and repository interfaces', 'IN_PROCESS', '2025-07-28', '2025-08-09', NULL),
('Fix NullPointerException in report module', 'Debug stack trace and add null checks', 'FINISHED', '2025-07-20', '2025-07-20', '2025-07-21'),
('Implement JWT authentication', 'Add JWT filter and update security config', 'FAILED', '2025-07-15', '2025-07-22', NULL),
('Write unit tests for UserService', 'Cover methods with JUnit and Mockito', 'CREATED', '2025-07-29', '2025-08-12', NULL),
('Optimize SQL queries', 'Review slow queries and add proper indexes', 'IN_PROCESS', '2025-07-25', '2025-08-05', NULL),
('Implement email notifications', 'Send email when task status changes', 'FINISHED', '2025-07-18', '2025-07-19', '2025-07-19'),
('Migrate project to Java 21', 'Update Maven dependencies and fix warnings', 'FAILED', '2025-07-10', '2025-07-15', NULL),
('Update API documentation', 'Write OpenAPI specification for REST endpoints', 'CREATED', '2025-07-30', '2025-08-10', NULL),
('Implement pagination for task list', 'Use Spring Data JPA Pageable in service', 'IN_PROCESS', '2025-07-27', '2025-08-06', NULL);