package qu.elec499.tau;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import qu.elec499.tau.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ResidentAdapter extends ArrayAdapter<Resident> {

    ResidentAdapter(@NonNull Context context, int resource, ArrayList<Resident> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = ((Activity) getContext()).
                    getLayoutInflater().inflate(R.layout.resident_tile, parent, false);

        TextView name = convertView.findViewById(R.id.nameTile);
        TextView phone = convertView.findViewById(R.id.phoneTile);
        TextView status = convertView.findViewById(R.id.statusTile);
        TextView pos = convertView.findViewById(R.id.NPTile);
        CircleImageView userImage = convertView.findViewById(R.id.photoTile);

        Resident resident = getItem(position);

        if (resident != null) {
            name.setText(resident.getName());
            phone.setText(resident.getPhone());
            status.setText(resident.getStatus());
            pos.setText(resident.getNP());
            if (resident.getPhotoURI() == null)
                Picasso.with(getContext())
                        .load(R.drawable.employee_tie)
                        .into(userImage);
            else
                Picasso.with(getContext())
                        .load(resident.getPhotoURI())
                        .error(R.drawable.employee_tie)
                        .into(userImage);
        }
        return convertView;
    }
}
