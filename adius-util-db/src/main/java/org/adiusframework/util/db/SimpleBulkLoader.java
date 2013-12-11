package org.adiusframework.util.db;

public class SimpleBulkLoader extends AbstractBulkLoader {

	public SimpleBulkLoader(String table, String columns, boolean open) {
		super(table, columns, open);
	}

	public void write(Object... values) {
		StringBuffer buf = new StringBuffer();
		int i = 0;
		for (Object obj : values) {
			i++;
			buf.append(obj.toString());
			buf.append((i % this.getColumnCount() != 0) ? this.getFieldTerminator() : (this.getLineTerminator()));
		}
		super.writeData(buf);
	}
}
