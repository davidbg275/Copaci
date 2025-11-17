-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 17-11-2025 a las 23:44:45
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `bd_copaci`
--
CREATE DATABASE IF NOT EXISTS `bd_copaci` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `bd_copaci`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `bitacora_asistencias`
--
-- Creación: 17-11-2025 a las 22:30:49
--

CREATE TABLE `bitacora_asistencias` (
  `id_asistencia` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `tipo_evento` varchar(20) NOT NULL,
  `asistencia` tinyint(1) NOT NULL,
  `porcentaje_asistencia` tinyint(1) NOT NULL,
  `id_ciudadano` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cargos`
--
-- Creación: 17-11-2025 a las 22:29:09
--

CREATE TABLE `cargos` (
  `id_cargo` int(11) NOT NULL,
  `cargo` varchar(30) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `cumplio` tinyint(1) NOT NULL,
  `fecha_registro` date NOT NULL,
  `id_ciudadano` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ciudadanos`
--
-- Creación: 17-11-2025 a las 22:08:56
-- Última actualización: 17-11-2025 a las 22:36:06
--

CREATE TABLE `ciudadanos` (
  `id_ciudadano` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `fecha_nacimiento` date NOT NULL,
  `domicilio_actual` varchar(50) NOT NULL,
  `manzana` varchar(20) NOT NULL,
  `estado_civil` varchar(20) NOT NULL,
  `ocupacion` varchar(20) NOT NULL,
  `tipo_ciudadano` varchar(20) NOT NULL,
  `fecha_alta` date NOT NULL,
  `tipo_certificado` varchar(20) NOT NULL,
  `lugar_nacimiento` varchar(20) NOT NULL,
  `grado_estudios` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `ciudadanos`
--

INSERT INTO `ciudadanos` (`id_ciudadano`, `nombre`, `fecha_nacimiento`, `domicilio_actual`, `manzana`, `estado_civil`, `ocupacion`, `tipo_ciudadano`, `fecha_alta`, `tipo_certificado`, `lugar_nacimiento`, `grado_estudios`) VALUES
(1, 'Manuel', '2000-09-20', 'Fco.mina', 'Sta', 'Soltero', 'Estudiante', 'Estudiante', '2025-11-14', 'n/a', 'Toluca', 'Preparatoria');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cooperaciones`
--
-- Creación: 17-11-2025 a las 22:23:21
--

CREATE TABLE `cooperaciones` (
  `id_cooperacion` int(11) NOT NULL,
  `anio` year(4) NOT NULL,
  `banda` int(11) NOT NULL,
  `castillo` int(11) NOT NULL,
  `paseo` int(11) NOT NULL,
  `fecha_registro` date NOT NULL,
  `descuento` varchar(10) NOT NULL,
  `total_pagado` double NOT NULL,
  `tipo_pago` varchar(10) NOT NULL,
  `id_ciudadano` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `donaciones`
--
-- Creación: 17-11-2025 a las 22:25:25
--

CREATE TABLE `donaciones` (
  `id_donacion` int(11) NOT NULL,
  `concepto` varchar(100) NOT NULL,
  `monto` double NOT NULL,
  `fecha` date NOT NULL,
  `id_ciudadano` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `faenas`
--
-- Creación: 17-11-2025 a las 22:26:59
--

CREATE TABLE `faenas` (
  `id_faena` int(11) NOT NULL,
  `anio` year(4) NOT NULL,
  `mes` varchar(12) NOT NULL,
  `asistencia` tinyint(1) NOT NULL,
  `pago_reposicion` double NOT NULL,
  `fecha_registro` date NOT NULL,
  `id_ciudadano` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `bitacora_asistencias`
--
ALTER TABLE `bitacora_asistencias`
  ADD PRIMARY KEY (`id_asistencia`);

--
-- Indices de la tabla `cargos`
--
ALTER TABLE `cargos`
  ADD PRIMARY KEY (`id_cargo`);

--
-- Indices de la tabla `ciudadanos`
--
ALTER TABLE `ciudadanos`
  ADD PRIMARY KEY (`id_ciudadano`);

--
-- Indices de la tabla `cooperaciones`
--
ALTER TABLE `cooperaciones`
  ADD PRIMARY KEY (`id_cooperacion`);

--
-- Indices de la tabla `donaciones`
--
ALTER TABLE `donaciones`
  ADD PRIMARY KEY (`id_donacion`);

--
-- Indices de la tabla `faenas`
--
ALTER TABLE `faenas`
  ADD PRIMARY KEY (`id_faena`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `bitacora_asistencias`
--
ALTER TABLE `bitacora_asistencias`
  MODIFY `id_asistencia` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `cargos`
--
ALTER TABLE `cargos`
  MODIFY `id_cargo` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `ciudadanos`
--
ALTER TABLE `ciudadanos`
  MODIFY `id_ciudadano` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `cooperaciones`
--
ALTER TABLE `cooperaciones`
  MODIFY `id_cooperacion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `donaciones`
--
ALTER TABLE `donaciones`
  MODIFY `id_donacion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `faenas`
--
ALTER TABLE `faenas`
  MODIFY `id_faena` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
