package com.xecko.util.dvds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Source {
	File source = null;
	
	public Source(String source) throws FileNotFoundException {
		this.source = new File(source);
		if (!this.source.exists()) {
			throw new FileNotFoundException("Source Directory [" + source
					+ "] does not exist");
		}
		if (!this.source.isDirectory()) {
			throw new FileNotFoundException("Source Directory [" + source
					+ "] is not a directory");
		}
	}

	class SizeComparator implements Comparator<Entry> {
		@Override
		public int compare(Entry e1, Entry e2) {
			long e1Size = e1.size();
			long e2Size = e2.size();

			if (e1Size > e2Size) {
				return -1;
			} else if (e1Size < e2Size) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	public ArrayList<Entry> getContents() throws IOException {
		String[] list = source.list();
		ArrayList<Entry> entries = new ArrayList<Entry>();
		for (String file : list) {
			if (file.equals(".DS_Store"))
				continue;
			entries.add(new Entry(source + "/" + file));
		}
		Collections.sort(entries, new SizeComparator());
		return entries;
	}
}
