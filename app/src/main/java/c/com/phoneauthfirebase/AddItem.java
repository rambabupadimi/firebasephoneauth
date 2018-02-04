package c.com.phoneauthfirebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.ButterKnife;
import butterknife.InjectView;
import c.com.phoneauthfirebase.models.*;

public class AddItem extends AppCompatActivity {


    @InjectView(R.id.itemName)
    EditText itemName;

    @InjectView(R.id.itemDescription)
    EditText itemDescription;

    @InjectView(R.id.itemPrice)
    EditText itemPrice;

    @InjectView(R.id.itemQuantity)
    EditText itemQuantity;

    @InjectView(R.id.itemSave)
    Button itemSave;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        ButterKnife.inject(this);
        initialiseIDS();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       final String id = getIntent().getStringExtra("id");

        itemSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ItemModel itemModel = new ItemModel();
                if(itemName.getText()!=null && itemName.getText().toString().trim().length()>0)
                {
                    itemModel.setName(itemName.getText().toString());
                }
                else
                {
                    return;
                }

                if(itemPrice.getText()!=null && itemPrice.getText().toString().trim().length()>0)
                {
                    itemModel.setPrice(Integer.parseInt(itemPrice.getText().toString()));
                }
                else
                {
                    return;
                }

                if(itemQuantity.getText()!=null && itemQuantity.getText().toString().trim().length()>0)
                {
                    itemModel.setQuantity(Integer.parseInt(itemQuantity.getText().toString()));

                }
                else
                {
                    return;
                }

                databaseReference.child(id).push().setValue(itemModel);
                databaseReference.keepSynced(true);
                Intent intent = new Intent(AddItem.this,Item.class);
                intent.putExtra("id",id);
                startActivity(intent);

            }
        });
    }
    private void initialiseIDS()
    {
        firebaseDatabase    =   FirebaseDatabase.getInstance();
        databaseReference   =  firebaseDatabase.getReference().child("Item");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()== android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
