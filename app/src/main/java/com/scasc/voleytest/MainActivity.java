package com.scasc.voleytest;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;

public class MainActivity extends AppCompatActivity {

    Button btnLimpiar,btnMostrar, btnAgregar, btnModificar, btnEliminar;
    EditText etID, etNombre, etPrecio, etCantidad;
    String idproducto, nombre, precio, cantidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLimpiar = (Button) findViewById(R.id.btnLimpiar);
        btnMostrar = (Button) findViewById(R.id.btnMostrar);
        btnAgregar = (Button) findViewById(R.id.btnAgregar);
        btnModificar = (Button) findViewById(R.id.btnModificar);
        btnEliminar = (Button) findViewById(R.id.btnEliminar);

        etID = (EditText) findViewById(R.id.etID);
        etNombre = (EditText) findViewById(R.id.etNombre);
        etPrecio = (EditText) findViewById(R.id.etPrecio);
        etCantidad = (EditText) findViewById(R.id.etCantidad);

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarTextos();
            }
        });

        btnMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                idproducto = etID.getText().toString().trim();

                String url = "https://jaahosting.com/apidata/recuperar_producto.php?idproducto=" + idproducto;
                recuperarData(url);
            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombre = etNombre.getText().toString().trim();
                precio = etPrecio.getText().toString().trim();
                cantidad = etCantidad.getText().toString().trim();

                String url = "https://jaahosting.com/apidata/insertar_producto.php?nombre="
                        + nombre + "&precio="
                        + precio + "&cantidad=" + cantidad;

                guardarData(url,0);

            }
        });

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idproducto = etID.getText().toString().trim();
                nombre = etNombre.getText().toString().trim();
                precio = etPrecio.getText().toString().trim();
                cantidad = etCantidad.getText().toString().trim();

                String url = "https://jaahosting.com/apidata/modificar_producto.php?nombre="
                        + nombre + "&precio="
                        + precio + "&cantidad=" + cantidad
                        + "&idproducto=" + idproducto;

                guardarData(url,1);
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idproducto = etID.getText().toString().trim();
                String url = "https://jaahosting.com/apidata/eliminar_producto.php?idproducto="
                        + idproducto;

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("¿Desea eliminar este producto?")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                guardarData(url,2);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();

            }
        });
    }

    private void recuperarData(String url) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Recuperando...");

        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            String success = jsonObject.getString("success");

                            if (success.equalsIgnoreCase("true")) {

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject object = jsonArray.getJSONObject(i);
                                    etNombre.setText(object.getString(
                                            "nombre"));
                                    etPrecio.setText(object.getString("precio"));
                                    etCantidad.setText(object.getString("cantidad"));

                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),
                                        "REGISTRO NO ENCONTRADO!",
                                        Toast.LENGTH_LONG).show();
                                limpiarTextos();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), error.getMessage(),
                        Toast.LENGTH_LONG).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

        limpiarCache(requestQueue);

    }

    private void guardarData(String url,int tipo) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Procesando...");

        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equalsIgnoreCase("false")) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), response,
                            Toast.LENGTH_LONG).show();

                } else {
                    progressDialog.dismiss();
                    if(tipo==2){
                        Toast.makeText(getApplicationContext(), "REGISTRO ELIMINADO!",
                                Toast.LENGTH_LONG).show();
                    }else if(tipo==0){
                        Toast.makeText(getApplicationContext(), "REGISTRO GUARDADO!",
                                Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "REGISTRO MODIFICADO!",
                                Toast.LENGTH_LONG).show();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), error.getMessage(),
                        Toast.LENGTH_LONG).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        limpiarCache(requestQueue);
        limpiarTextos();

    }

    private void limpiarCache(RequestQueue requestQueue) {
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void limpiarTextos() {
        etID.setText(null);
        etNombre.setText(null);
        etPrecio.setText(null);
        etCantidad.setText(null);
    }
}