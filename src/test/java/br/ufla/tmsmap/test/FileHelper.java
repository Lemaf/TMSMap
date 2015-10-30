package br.ufla.tmsmap.test;

import org.assertj.core.api.Condition;

import java.io.File;

/**
 * Created by rthoth on 30/10/15.
 */
public class FileHelper {
	public static final Condition<? super File> HAS_SOME_CONTENT = new Condition<File>() {
		@Override
		public boolean matches(File value) {
			return value.length() > 0;
		}
	};
}
