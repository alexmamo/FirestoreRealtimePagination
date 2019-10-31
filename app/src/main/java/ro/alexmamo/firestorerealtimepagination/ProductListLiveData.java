package ro.alexmamo.firestorerealtimepagination;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import static ro.alexmamo.firestorerealtimepagination.Constants.LIMIT;

@SuppressWarnings("ConstantConditions")
public class ProductListLiveData extends LiveData<Operation> implements EventListener<QuerySnapshot> {
    private Query query;
    private ListenerRegistration listenerRegistration;
    private OnLastVisibleProductCallback onLastVisibleProductCallback;
    private OnLastProductReachedCallback onLastProductReachedCallback;

    ProductListLiveData(Query query, OnLastVisibleProductCallback onLastVisibleProductCallback, OnLastProductReachedCallback onLastProductReachedCallback) {
        this.query = query;
        this.onLastVisibleProductCallback = onLastVisibleProductCallback;
        this.onLastProductReachedCallback = onLastProductReachedCallback;
    }

    @Override
    protected void onActive() {
        listenerRegistration = query.addSnapshotListener(this);
    }

    @Override
    protected void onInactive() {
        listenerRegistration.remove();
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) return;

        for (DocumentChange documentChange : querySnapshot.getDocumentChanges()) {
            switch (documentChange.getType()) {
                case ADDED:
                    Product addedProduct = documentChange.getDocument().toObject(Product.class);
                    Operation addOperation = new Operation(addedProduct, R.string.added);
                    setValue(addOperation);
                    break;

                case MODIFIED:
                    Product modifiedProduct = documentChange.getDocument().toObject(Product.class);
                    Operation modifyOperation = new Operation(modifiedProduct, R.string.modified);
                    setValue(modifyOperation);
                    break;

                case REMOVED:
                    Product removedProduct = documentChange.getDocument().toObject(Product.class);
                    Operation removeOperation = new Operation(removedProduct, R.string.removed);
                    setValue(removeOperation);
            }
        }

        int querySnapshotSize = querySnapshot.size();
        if (querySnapshotSize < LIMIT) {
            onLastProductReachedCallback.setLastProductReached(true);
        } else {
            DocumentSnapshot lastVisibleProduct = querySnapshot.getDocuments().get(querySnapshotSize - 1);
            onLastVisibleProductCallback.setLastVisibleProduct(lastVisibleProduct);
        }
    }

    interface OnLastVisibleProductCallback {
        void setLastVisibleProduct(DocumentSnapshot lastVisibleProduct);
    }

    interface OnLastProductReachedCallback {
        void setLastProductReached(boolean isLastProductReached);
    }
}