package io.github.sceneview.sample.arcursorplacement;

import android.util.Log
import android.view.LayoutInflater
import android.view.View;
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.sceneview.sample.arcursorplacement.Model




class ModelsAdapter(private val items: List<Model>, private val listener: OnModelClickListener) : RecyclerView.Adapter<ModelsAdapter.ViewHolder> (){

    class ViewHolder(itemView:View) :RecyclerView.ViewHolder(itemView){

        val modelImage:ImageView=itemView.findViewById(R.id.modelImage)
        val modelName:TextView = itemView.findViewById(R.id.modelName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.e("ModelsAdapter","onCreateViewHolder")
        val view= LayoutInflater.from(parent.context).inflate(R.layout.modelsview_item,parent,false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e("ModelsAdapter","onBindViewHolder")
        val model=items[position]
        model.modelImage?.let { holder.modelImage.setImageBitmap(model.modelImage) }
        holder.modelName.text=model.displayName
        holder.itemView.setOnClickListener {
            listener.onModelClick(model)
        }
    }
    override fun getItemCount(): Int {
        Log.e("ModelsAdapter","getItemCount:"+items.size)
        return items.size

    }


}