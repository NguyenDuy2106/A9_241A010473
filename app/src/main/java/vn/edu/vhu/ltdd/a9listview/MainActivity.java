package vn.edu.vhu.ltdd.a9listview;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "A9_241A010473";

    private final List<MonAn> danhSachGoc = new ArrayList<>();
    private final List<MonAn> danhSachHienThi = new ArrayList<>();
    private MonAnAdapter adapter;

    private ListView lvMonAn;
    private TextView tvTrong;
    private EditText edtTen, edtGia, edtTimKiem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        lvMonAn = findViewById(R.id.lvMonAn);
        tvTrong = findViewById(R.id.tvTrong);
        edtTen = findViewById(R.id.edtTen);
        edtGia = findViewById(R.id.edtGia);
        edtTimKiem = findViewById(R.id.edtTimKiem);
        Button btnThem = findViewById(R.id.btnThem);

        taoDuLieuMau();
        danhSachHienThi.addAll(danhSachGoc);

        // ---- Bước then chốt: gắn adapter vào ListView ----
        adapter = new MonAnAdapter(this, danhSachHienThi);
        lvMonAn.setAdapter(adapter);

        // ListView có sẵn cơ chế hiện View thay thế khi danh sách rỗng
        lvMonAn.setEmptyView(tvTrong);

        // NC1: Lắng nghe sự kiện gõ phím ở ô tìm kiếm để lọc danh sách
        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                locDanhSach(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Bấm một dòng
        lvMonAn.setOnItemClickListener((parent, view, position, id) -> {
            MonAn mon = (MonAn) parent.getItemAtPosition(position);
            Toast.makeText(this,
                    getString(R.string.chon_mon, mon.getTen()), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Chọn dòng " + position + ": " + mon.getTen());
        });

        // Giữ lâu một dòng để xóa.
        lvMonAn.setOnItemLongClickListener((parent, view, position, id) -> {
            MonAn mon = danhSachHienThi.remove(position);
            danhSachGoc.remove(mon);
            adapter.notifyDataSetChanged();     // BÁO cho adapter biết dữ liệu đã đổi
            Toast.makeText(this,
                    getString(R.string.da_xoa, mon.getTen()), Toast.LENGTH_SHORT).show();
            return true;                        // true = đã xử lý, đừng gọi tiếp onItemClick
        });

        btnThem.setOnClickListener(v -> themMon());
    }

    /** NC1: Lọc danh sách món ăn theo tên khi tìm kiếm */
    private void locDanhSach(String query) {
        danhSachHienThi.clear();
        if (query.trim().isEmpty()) {
            danhSachHienThi.addAll(danhSachGoc);
        } else {
            String q = query.toLowerCase().trim();
            for (MonAn mon : danhSachGoc) {
                if (mon.getTen().toLowerCase().contains(q)) {
                    danhSachHienThi.add(mon);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void taoDuLieuMau() {
        danhSachGoc.add(new MonAn("Phở bò tái", "Nước dùng ninh xương 8 giờ", 45000, R.drawable.mon1));
        danhSachGoc.add(new MonAn("Bún chả Hà Nội", "Chả nướng than hoa", 50000, R.drawable.mon2));
        danhSachGoc.add(new MonAn("Cơm tấm sườn", "Sườn nướng mật ong", 40000, R.drawable.mon3));
        danhSachGoc.add(new MonAn("Bánh mì thịt nguội", "Pate nhà làm", 25000, R.drawable.mon4));
        danhSachGoc.add(new MonAn("Trà đào cam sả", "Mát lạnh, ít đường", 30000, R.drawable.mon5));
    }

    /** Đọc dữ liệu từ hai ô nhập, kiểm tra hợp lệ rồi thêm vào danh sách. */
    private void themMon() {
        String ten = edtTen.getText().toString().trim();
        String giaStr = edtGia.getText().toString().trim();

        if (ten.isEmpty()) {
            edtTen.setError(getString(R.string.err_ten));
            edtTen.requestFocus();
            return;
        }
        int gia;
        try {
            gia = Integer.parseInt(giaStr);
        } catch (NumberFormatException e) {
            edtGia.setError(getString(R.string.err_gia));
            edtGia.requestFocus();
            return;
        }
        if (gia <= 0) {
            edtGia.setError(getString(R.string.err_gia_duong));
            edtGia.requestFocus();
            return;
        }

        MonAn monMoi = new MonAn(ten, getString(R.string.moi_them), gia, R.drawable.mon1);
        danhSachGoc.add(0, monMoi);
        locDanhSach(edtTimKiem.getText().toString());
        lvMonAn.smoothScrollToPosition(0);      // cuộn lên để người dùng thấy món vừa thêm

        edtTen.setText("");
        edtGia.setText("");
        edtTen.requestFocus();
        Log.d(TAG, "Đã thêm: " + ten + " — tổng " + danhSachGoc.size() + " món");
    }
}
