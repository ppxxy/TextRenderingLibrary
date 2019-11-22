package tools.formulas;

import java.util.ArrayList;
import java.util.List;

public class MergeSortList<T extends Comparable<T>> {

	public void sort(List<T> values){
		mergeSort(0, values.size() - 1, values, new ArrayList<T>(values));
	}

	private void mergeSort(int low, int high, List<T> values, List<T> aux) {
		
		if(low < high){
			int mid = low + (high - low) / 2;
			mergeSort(low, mid, values, aux); 
			mergeSort(mid+1, high, values, aux);
			merge(low, mid, high, values, aux);
		}
	}

	private void merge(int low, int mid, int high, List<T> values, List<T> aux) {

		int left = low;
		int right = mid + 1;

		for(int i = low; i <= high; i++){
			aux.set(i, values.get(i));
		}

		while(left <= mid && right <= high){
			values.set(low++, aux.get(left).compareTo(aux.get(right)) < 0 ? aux.get(left++) : aux.get(right++));
		}

		while(left <= mid){
			values.set(low++, aux.get(left++));
		}
	}
	
}
