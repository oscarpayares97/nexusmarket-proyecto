# Services

## Introduction

This document gives a conceptual overview of the domain services of the NexusMarket marketplace. Each service is a plain Java class without framework annotations; it receives the authenticated user (the actor), validates its role, applies the business rules and talks to the outside world only through output ports.

The detailed definition of every service (authorization, collaborators, rules, exceptions and tests) is generated from the source code and lives in one document per subdomain under `SDD/domain/services/`.

---

# User and Authentication Management Services

Detail: `services/user-authentication-services.md`

## Change User Password

Changes the password of the authenticated user after verifying the current one.

## Change User Status

Activates, deactivates or blocks a user. An administrator cannot change its own status.

## Consult User

Lists the system users for back-office roles.

## Load Authenticated User

Resolves the persisted user that owns a valid token so that every request is evaluated against the current database state.

## Login

Authenticates a user with email and password and issues a signed JWT.

## Register Internal User

Creates an internal system user (administrator, supervisor or logistics operator). Restricted to administrators.

---

# Buyer Management Services

Detail: `services/buyer-services.md`

## Add Buyer Address

Adds an additional delivery address to the authenticated buyer.

## Change Buyer Commercial Status

Enables or suspends the ability of a buyer to purchase.

## Register Buyer

Registers a new buyer user together with the buyer profile and its primary delivery address.

---

# Seller Management Services

Detail: `services/seller-services.md`

## Register Seller

Registers a seller user, its trade name and its first warehouse in a single operation. Restricted to administrators.

---

# Catalog Management Services

Detail: `services/catalog-services.md`

## Add Product Variant

Adds a variant (for example size or color) to a product of the authenticated seller.

## Consult Published Products

Lists the published products of the catalog. Available without authentication.

## Discontinue Product

Permanently retires a product from sale.

## Owned Product Loader

Internal helper shared by the catalog services: checks that the actor is a seller, loads the product and verifies that the seller owns it.

## Publish Product

Publishes a product so that it appears in the public catalog.

## Register Product

Creates a product owned by the authenticated seller.

## Suspend Product

Temporarily removes a product from the public catalog.

---

# Warehouse Management Services

Detail: `services/warehouse-services.md`

## Change Warehouse Status

Activates or deactivates a warehouse.

## Consult Warehouses

Lists the warehouses visible to the requesting role.

## Create Warehouse

Creates a warehouse, either the marketplace warehouse or a warehouse that belongs to a seller.

---

# Inventory Management Services

Detail: `services/inventory-services.md`

## Adjust Inventory

Sets the available quantity of a product in a warehouse to a corrected value and records the adjustment movement.

## Change Inventory Status

Changes the operational status of an inventory record.

## Consult Inventory Movements

Lists the inventory movements (entries, reservations, sales, adjustments, returns).

## Consult Inventory

Retrieves the stock of a product across warehouses.

## Inventory Operation Guard

Internal helper shared by the inventory services: validates the role (seller or logistics operator), that the product is physical and the warehouse is active, that a seller only operates its own products and warehouses, and loads or creates the inventory record.

## Register Inventory Entry

Registers stock entering a warehouse for a product and records the movement.

## Register Inventory Return

Returns stock to inventory when a returned item is received. Internal service.

## Register Inventory Sale

Records the stock exit of the items of an order when its shipment is dispatched. Internal service.

## Reserve Inventory

Reserves stock for the items of an order when the order is confirmed. Internal service.

---

# Order Management Services

Detail: `services/order-services.md`

## Add Item To Cart

Adds a product to the open cart of the authenticated buyer, creating the cart if it does not exist.

## Confirm Order

Confirms the cart as an order with a delivery address and reserves the stock of its items.

## Consult My Orders

Lists the orders of the authenticated buyer.

## Consult Orders

Lists every order for back-office roles.

## Pay Order

Registers the payment of an order, issues its invoice and delivers digital products immediately.

## Remove Item From Cart

Removes a product from the open cart of the authenticated buyer.

---

# Billing Services

Detail: `services/billing-services.md`

## Consult Invoice

Retrieves the invoice of an order for its owner or for a back-office role.

## Consult Invoices

Lists every issued invoice for back-office roles.

## Issue Invoice

Issues the invoice of a paid order. An order can only be invoiced once. Internal service.

---

# Shipping Services

Detail: `services/shipping-services.md`

## Confirm Delivery

Confirms the delivery of a shipment and marks the order as delivered.

## Consult Shipment

Retrieves the shipment of an order for its owner or for a back-office role.

## Consult Shipments

Lists every shipment for back-office roles.

## Dispatch Shipment

Dispatches a packed shipment with its carrier and tracking number, marks the order as shipped and records the inventory sale.

## Pack Shipment

Marks a shipment as packed and ready to be dispatched.

## Register Shipment In Transit

Marks a dispatched shipment as in transit.

## Start Shipment Preparation

Creates the shipment of a paid order and starts its preparation.

---

# Return and Refund Services

Detail: `services/return-services.md`

## Approve Return

Approves a pending return request so that the buyer can send the items back.

## Consult My Returns

Lists the return requests of the authenticated buyer.

## Consult Refunds

Lists every refund for back-office roles.

## Consult Returns

Lists every return request for back-office roles.

## Process Refund

Processes a pending refund. A refund can only be processed once.

## Receive Return

Registers the physical reception of an approved return, returns the stock to inventory and creates the pending refund.

## Reject Return

Rejects a return request recording the rejection reason.

## Request Return

Creates a return request for delivered items of an order of the authenticated buyer.

---

# Reporting Services

Detail: `services/report-services.md`

## Generate Sales Report

Builds the sales report: gross sales, refunded amount, net sales, orders by status and sales by seller.

---

# Authorization Services

Detail: `services/authorization-services.md`

## Validate Ownership

Verifies that the acting user is the owner of the resource being accessed.

## Validate Role Authorization

Verifies that the acting user exists, is active and holds one of the roles allowed for an operation (RG-01, RG-03).

---
