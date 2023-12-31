package qu.elec499.tau;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import qu.elec499.tau.R;

public class ViolationViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView npHolder;
    View v;


    public ViolationViewHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image_single_view);
        npHolder = itemView.findViewById(R.id.violation_NP);
        v = itemView;

    }
}
