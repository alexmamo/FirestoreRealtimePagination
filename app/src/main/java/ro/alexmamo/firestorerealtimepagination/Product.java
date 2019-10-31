package ro.alexmamo.firestorerealtimepagination;

import androidx.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class Product {
    public String id;
    public String name;
    public double price;

    public Product() {}

    public Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}