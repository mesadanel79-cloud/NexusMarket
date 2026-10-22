# Especificación Funcional: NexusMarket

> Transcripción en Markdown de *Especificación Funcional del Negocio - NexusMarket.docx* (documento base del curso). Es la fuente de requisitos que implementa este repositorio. Al final se agrega una sección de **decisiones de interpretación** tomadas donde el documento no da detalle.

## 1. Introducción y Contexto
NexusMarket es una plataforma digital centralizada que actúa como intermediario comercial entre compradores y vendedores. El sistema administra integralmente la operación, desde el registro de usuarios y publicación de productos hasta la logística, facturación y posventa, garantizando trazabilidad y coordinación entre todos los participantes.

## 2. Objetivos y Alcance
| Código | Objetivo Funcional |
| --- | --- |
| OBJ-01 | Administrar la información de todos los usuarios del Marketplace. |
| OBJ-02 | Gestionar el registro y administración de vendedores. |
| OBJ-03 | Administrar compradores registrados. |
| OBJ-04 | Controlar la información de las bodegas. |
| OBJ-05 | Gestionar el catálogo de productos. |
| OBJ-06 | Administrar el inventario distribuido. |
| OBJ-07 | Gestionar el carrito de compras. |
| OBJ-08 | Controlar el ciclo completo de los pedidos. |
| OBJ-09 | Administrar la facturación de las compras. |
| OBJ-10 | Gestionar los procesos logísticos. |
| OBJ-11 | Administrar devoluciones y reembolsos. |
| OBJ-12 | Consolidar información administrativa para consulta. |

## 3. Alcance del Sistema
El sistema administrará exclusivamente los procesos descritos en esta especificación funcional.

### 3.1 Procesos Incluidos
Registro de compradores · Administración de usuarios · Registro administrativo de vendedores · Administración de bodegas · Gestión del catálogo de productos · Administración del inventario · Gestión del carrito de compras · Gestión de pedidos · Gestión de facturación · Gestión de envíos · Gestión de devoluciones · Gestión de reembolsos · Consulta de reportes administrativos.

### 3.2 Procesos Fuera del Alcance
El documento no contempla interfaces gráficas, aplicaciones móviles, portales web, mecanismos de autenticación técnica, tecnologías de implementación, arquitectura del software ni detalles de almacenamiento (estos aspectos se definen en el resto del `SDD/`, siguiendo la arquitectura del proyecto base del curso).

## 4. Descripción General del Marketplace
La organización facilita la comercialización de productos de terceros mediante una plataforma empresarial unificada.

| Componente | Descripción |
| --- | --- |
| Usuarios | Personas autorizadas para interactuar con el sistema. |
| Vendedores | Responsables de comercializar productos. |
| Compradores | Usuarios que realizan compras. |
| Bodegas | Lugares donde se administra el inventario físico. |
| Productos | Bienes físicos o digitales ofrecidos. |
| Inventario | Existencias disponibles para comercialización. |
| Pedidos | Solicitudes de compra realizadas por compradores. |
| Facturación | Información comercial asociada a las ventas. |
| Envíos | Procesos logísticos para productos físicos. |

## 5. Participantes del Negocio
Cada participante desempeña un único rol dentro del sistema y únicamente podrá interactuar con la información correspondiente a sus funciones.

| Participante | Descripción General | Rol en el código |
| --- | --- | --- |
| Comprador | Persona que adquiere productos publicados. | `BUYER` |
| Vendedor | Responsable de registrar y administrar sus productos. | `SELLER` |
| Operador Logístico | Encargado de la operación física de bodegas y despachos. | `LOGISTIC_OPERATOR` |
| Administrador | Responsable de la administración de vendedores y bodegas. | `ADMIN` |
| Supervisor | Perfil de consulta y seguimiento operativo. | `SUPERVISOR` |

## 6. Modelo Operativo del Marketplace
### 6.1 Flujo General del Negocio
1. **Incorporación:** el Administrador registra al vendedor y su primera bodega.
2. **Catálogo:** el vendedor registra productos y define sus características.
3. **Inventario:** se registran existencias iniciales en las bodegas asociadas.
4. **Publicación:** los productos se hacen visibles en el catálogo público.
5. **Compra:** el comprador selecciona productos mediante el carrito y confirma el pedido.
6. **Transacción:** se valida el pago y se inicia el flujo de preparación.
7. **Logística:** se realiza el empaque, despacho y transporte del pedido.
8. **Cierre:** el pedido se marca como finalizado tras la entrega confirmada.

## Dominio 1. Administración de Usuarios
Base de autenticación e identificación. Objetivo: garantizar la correcta identificación y estado operativo de los usuarios. Participantes: todos los roles.

| Atributo | Descripción Funcional |
| --- | --- |
| Identificador | Identifica de forma única al usuario. |
| Nombre completo | Nombre oficial del usuario. |
| Correo electrónico | Medio principal de acceso y comunicación. |
| Rol | Define las responsabilidades y permisos. |
| Estado | Condición operativa (Activo, Bloqueado, etc.). |

## Dominio 2. Gestión de Compradores
Objetivo: administrar la información específica para la participación en procesos comerciales. **Restricción clave:** el comprador nunca administrará información de otros compradores ni inventarios.

| Atributo | Descripción |
| --- | --- |
| Dirección principal | Ubicación habitual para entregas. |
| Direcciones adicionales | Ubicaciones secundarias de entrega. |
| Estado comercial | Condición del comprador para realizar compras. |

## Dominio 3. Gestión de Vendedores
Objetivo: administrar la incorporación y mantenimiento de proveedores. **Regla:** los vendedores no pueden auto-registrarse; son incorporados por el Administrador.

## Dominio 4. Gestión de Bodegas
Objetivo: controlar los espacios físicos de almacenamiento. Se distinguen **bodegas del Marketplace** y **bodegas de Vendedores**.

## Dominio 5. Gestión del Catálogo
El catálogo diferencia productos **físicos** (requieren inventario y despacho) y **digitales** (entrega inmediata tras pago).

| Atributo | Descripción |
| --- | --- |
| Tipo de Producto | Físico o Digital. |
| Variantes | Diferencias de color, talla, modelo, etc. |
| Estado | Publicado, Suspendido o Descontinuado. |

## Dominio 6. Gestión del Inventario
El inventario es distribuido y debe estar vinculado obligatoriamente a un producto y una bodega específica. Movimientos: **Ingreso, Reserva, Salida por venta, Ajuste y Devolución**. No se permitirán existencias negativas bajo ninguna circunstancia.

## Dominio 7. Gestión de Pedidos
Compromiso comercial formal; su ciclo de vida es el proceso central.

| Estado | Significado |
| --- | --- |
| Carrito | Selección provisional de productos. |
| Pendiente de Pago | Espera de confirmación financiera. |
| Pagado | Inicio de procesos de alistamiento. |
| Despachado | Salida física de la bodega. |
| Entregado / Finalizado | Conclusión satisfactoria de la entrega. |

## 10. Restricciones Generales
| Código | Restricción |
| --- | --- |
| RG-01 | Toda operación debe ejecutarse por un usuario autenticado. |
| RG-02 | Cada usuario tendrá un único rol dentro del sistema. |
| RG-03 | Ningún participante podrá administrar información fuera de su rol. |

## 11. Validaciones Críticas
* **Inventario:** no se puede reservar inventario inexistente o marcado como "Dañado".
* **Pedidos:** un pedido finalizado no podrá ser modificado bajo ninguna circunstancia.
* **Usuarios:** el documento de identidad y el correo electrónico deben ser únicos en la plataforma.

## 12. Matriz de Responsabilidades
| Proceso | Comprador | Vendedor | Op. Logístico | Admin |
| --- | :---: | :---: | :---: | :---: |
| Registro Vendedores | | | | ✔ |
| Registro Productos | | ✔ | | |
| Administración Inventario | | ✔ | ✔ | |
| Gestión de Pedidos | ✔ | ✔ | ✔ | |
| Gestión Reembolsos | ✔ | | | ✔ |

---

## Anexo: decisiones de interpretación (donde el documento no da detalle)
| Tema | Decisión | Justificación |
| --- | --- | --- |
| Supervisor | Solo consulta (reportes, pedidos, productos, inventario, devoluciones, bitácora). | "Perfil de consulta y seguimiento operativo"; no aparece en la matriz de responsabilidades. |
| Devoluciones | El comprador solicita, el Administrador aprueba/rechaza y reembolsa, el Operador Logístico recibe el producto en bodega. | Matriz §12 (reembolsos: Comprador y Admin) + operación física de bodegas del Operador. |
| Inventario del vendedor | El vendedor administra solo el inventario de **sus** productos; en bodegas propias o del marketplace. | Matriz §12 + RG-03. Una bodega SELLER solo guarda productos de su dueño. |
| Producto "Dañado" | `InventoryItem.damagedQuantity`; se alimenta con ajustes o devoluciones con motivo `DEFECTIVE`. | Validación crítica §11. |
| Cancelación de pedidos | Estado adicional `CANCELLED` (cancelación antes del pago o vencimiento a los 60 min) con movimiento `RESERVATION_RELEASE`. | Sin él, el stock reservado de pedidos no pagados quedaría bloqueado para siempre. |
| Estado "Carrito" | El `Cart` guarda la selección provisional; el `Order` nace en `CART` al confirmar y pasa a `PENDING_PAYMENT` al reservar inventario. | Mantiene el ciclo de estados de la especificación. |
| Pago | Simulado (sin pasarela externa); se registra el medio de pago. | La tecnología de pagos está fuera del alcance. |
| Facturación | Una factura por pedido, precios con IVA incluido (19 % configurable). | OBJ-09. |
| Dirección de entrega | Solo la dirección principal o una adicional del comprador. | Dominio 2. |
