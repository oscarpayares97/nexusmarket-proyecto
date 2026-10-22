package application.infrastructure.seed;

import application.domain.models.Buyer;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.WarehouseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Datos iniciales para probar el sistema al arrancar. Idempotente.
 * Clave de todos los usuarios: Password123*
 */
@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);
    private static final String PASSWORD = "Password123*";
    private static final String ADMIN_EMAIL = "admin@nexusmarket.com";

    private final UserRepositoryPort users;
    private final BuyerRepositoryPort buyers;
    private final SellerRepositoryPort sellers;
    private final WarehouseRepositoryPort warehouses;
    private final ProductRepositoryPort products;
    private final InventoryRepositoryPort inventories;
    private final PasswordServicePort passwordService;

    public DatabaseSeeder(UserRepositoryPort users, BuyerRepositoryPort buyers,
                          SellerRepositoryPort sellers, WarehouseRepositoryPort warehouses,
                          ProductRepositoryPort products, InventoryRepositoryPort inventories,
                          PasswordServicePort passwordService) {
        this.users = users;
        this.buyers = buyers;
        this.sellers = sellers;
        this.warehouses = warehouses;
        this.products = products;
        this.inventories = inventories;
        this.passwordService = passwordService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (users.existsByEmail(ADMIN_EMAIL)) {
            log.info("Seeds already loaded, skipping.");
            return;
        }
        log.info("Loading seed data (one user per role)...");

        createUser("ADM-0001", "Ana Administradora", ADMIN_EMAIL, UserRole.ADMINISTRATOR);
        createUser("SUP-0001", "Sergio Supervisor", "supervisor@nexusmarket.com", UserRole.SUPERVISOR);
        createUser("LOG-0001", "Laura Logistica", "logistica@nexusmarket.com", UserRole.LOGISTICS_OPERATOR);

        User buyerUser = createUser("1001234567", "Carlos Comprador", "comprador@nexusmarket.com", UserRole.BUYER);
        buyers.save(new Buyer(buyerUser.getId(), "Calle 10 #20-30, Medellin"));

        User sellerUser = createUser("900123456", "Sofia Vendedora", "vendedor@nexusmarket.com", UserRole.SELLER);
        Seller seller = sellers.save(new Seller(sellerUser.getId(), "TechStore SAS"));

        warehouses.save(new Warehouse(null, "Bodega Central Marketplace", "Medellin - Zona Industrial",
                WarehouseType.MARKETPLACE, null));
        Warehouse sellerWarehouse = warehouses.save(new Warehouse(null, "Bodega TechStore", "Bogota - Fontibon",
                WarehouseType.SELLER, seller.getUserId()));

        Product headphones = createPublishedProduct(seller, "Audifonos Bluetooth", ProductType.PHYSICAL, "150000");
        Product keyboard = createPublishedProduct(seller, "Teclado mecanico", ProductType.PHYSICAL, "320000");
        createPublishedProduct(seller, "Curso de Java (digital)", ProductType.DIGITAL, "89000");

        addStock(headphones, sellerWarehouse, 50);
        addStock(keyboard, sellerWarehouse, 20);

        log.info("Seeds OK. Users: admin@/supervisor@/logistica@/comprador@/vendedor@nexusmarket.com (password {})",
                PASSWORD);
    }

    private User createUser(String document, String fullName, String email, UserRole role) {
        return users.save(new User(null, document, fullName, email, passwordService.encode(PASSWORD), role));
    }

    private Product createPublishedProduct(Seller seller, String name, ProductType type, String price) {
        Product product = new Product(null, seller.getUserId(), name, type, Money.of(price));
        product.publish();
        return products.save(product);
    }

    private void addStock(Product product, Warehouse warehouse, int units) {
        Inventory inventory = new Inventory(null, product.getId(), warehouse.getId());
        inventory.registerEntry(units);
        inventories.save(inventory);
    }
}