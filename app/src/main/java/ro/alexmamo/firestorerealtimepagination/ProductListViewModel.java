package ro.alexmamo.firestorerealtimepagination;

import androidx.lifecycle.ViewModel;

@SuppressWarnings("WeakerAccess")
public class ProductListViewModel extends ViewModel {
    private ProductListRepository productListRepository = new FirestoreProductListRepositoryCallback();

    ProductListLiveData getProductListLiveData() {
        return productListRepository.getProductListLiveData();
    }

    interface ProductListRepository {
        ProductListLiveData getProductListLiveData();
    }
}