package ro.alexmamo.firestorerealtimepagination;

import android.os.Bundle;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    private List<Product> productList = new ArrayList<>();
    private RecyclerView productsRecyclerView;
    private ProductsAdapter productsAdapter;
    private ProductListViewModel productListViewModel;
    private boolean isScrolling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        initProductsRecyclerView();
        initProductsAdapter();
        initProductListViewModel();
        getProducts();
        initRecyclerViewOnScrollListener();
    }

    private void initProductsRecyclerView(){
        productsRecyclerView = findViewById(R.id.products_recycler_view);
    }

    private void initProductsAdapter() {
        productsAdapter = new ProductsAdapter(productList);
        productsRecyclerView.setAdapter(productsAdapter);
    }

    private void initProductListViewModel() {
        productListViewModel = new ViewModelProvider(this).get(ProductListViewModel.class);
    }

    private void getProducts() {
        ProductListLiveData productListLiveData = productListViewModel.getProductListLiveData();
        if (productListLiveData != null) {
            productListLiveData.observe(this, operation -> {
                switch (operation.type) {
                    case R.string.added:
                        Product addedProduct = operation.product;
                        addProduct(addedProduct);
                        break;

                    case R.string.modified:
                        Product modifiedProduct = operation.product;
                        modifyProduct(modifiedProduct);
                        break;

                    case R.string.removed:
                        Product removedProduct = operation.product;
                        removeProduct(removedProduct);
                }
                productsAdapter.notifyDataSetChanged();
            });
        }
    }

    private void addProduct(Product addedProduct) {
        productList.add(addedProduct);
    }

    private void modifyProduct(Product modifiedProduct) {
        for (int i = 0; i < productList.size(); i++) {
            Product currentProduct = productList.get(i);
            if (currentProduct.id.equals(modifiedProduct.id)) {
                productList.remove(currentProduct);
                productList.add(i, modifiedProduct);
            }
        }
    }

    private void removeProduct(Product removedProduct) {
        for (int i = 0; i < productList.size(); i++) {
            Product currentProduct = productList.get(i);
            if (currentProduct.id.equals(removedProduct.id)) {
                productList.remove(currentProduct);
            }
        }
    }

    private void initRecyclerViewOnScrollListener() {
        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                if (layoutManager != null) {
                    int firstVisibleProductPosition = layoutManager.findFirstVisibleItemPosition();
                    int visibleProductCount = layoutManager.getChildCount();
                    int totalProductCount = layoutManager.getItemCount();

                    if (isScrolling && (firstVisibleProductPosition + visibleProductCount == totalProductCount)) {
                        isScrolling = false;
                        getProducts();
                    }
                }
            }
        };
        productsRecyclerView.addOnScrollListener(onScrollListener);
    }
}