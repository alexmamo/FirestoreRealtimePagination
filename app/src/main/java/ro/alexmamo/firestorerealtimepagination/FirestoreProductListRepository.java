package ro.alexmamo.firestorerealtimepagination;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static com.google.firebase.firestore.Query.Direction.ASCENDING;
import static ro.alexmamo.firestorerealtimepagination.Constants.LIMIT;
import static ro.alexmamo.firestorerealtimepagination.Constants.PRODUCTS_COLLECTION;
import static ro.alexmamo.firestorerealtimepagination.Constants.PRODUCT_NAME_PROPERTY;

public class FirestoreProductListRepository implements ProductListViewModel.ProductListRepository,
        ProductListLiveData.OnLastVisibleProductCallback, ProductListLiveData.OnLastProductReached {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference productsRef = firebaseFirestore.collection(PRODUCTS_COLLECTION);
    private Query query = productsRef.orderBy(PRODUCT_NAME_PROPERTY, ASCENDING).limit(LIMIT);
    private DocumentSnapshot lastVisibleProduct;
    private boolean isLastProductReached;

    @Override
    public ProductListLiveData getProductListLiveData() {
        if (isLastProductReached) {
            return null;
        }
        if (lastVisibleProduct != null) {
            query = query.startAfter(lastVisibleProduct);
        }
        return new ProductListLiveData(query, this, this);
    }

    @Override
    public void setLastVisibleProduct(DocumentSnapshot lastVisibleProduct) {
        this.lastVisibleProduct = lastVisibleProduct;
    }

    @Override
    public void setLastProductReached(boolean isLastProductReached) {
        this.isLastProductReached = isLastProductReached;
    }
}