package nu.wasis.mvz.gui;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import nu.wasis.mvz.CopyRecommender;
import nu.wasis.mvz.util.DirInfoCacher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.ProgressListener;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class MainWindow {

	private static final String DEFAULT_TOTAL = "not so sure";

	private static final String DEFAULT_CURRENT = "don't know";

	private static final Logger LOG = Logger.getLogger(MainWindow.class);
	
	protected Shell shell;
	private Text txtSource;
	private Text txtTarget;
	
	private String currentString = DEFAULT_CURRENT;
	private String totalString = DEFAULT_TOTAL;

	private Tree tree;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	protected void createContents() {
		shell = new Shell();
		shell.setMinimumSize(new Point(400, 300));
		shell.setSize(404, 300);
		shell.setText("mvz");
		shell.setLayout(new GridLayout(2, false));
		
		tree = new Tree(shell, SWT.BORDER | SWT.CHECK);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		txtSource = new Text(shell, SWT.BORDER);
		txtSource.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtSource.setText(FileUtils.getUserDirectoryPath());
		// txtSource.setText("/xmpl/series");
		
		Button btnSelectSource = new Button(shell, SWT.NONE);
		GridData gd_btnSelectSource = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnSelectSource.widthHint = 100;
		btnSelectSource.setLayoutData(gd_btnSelectSource);
		btnSelectSource.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectDirectory("Select Source Directory", txtSource);
			}
		});
		btnSelectSource.setText("Source Dir");
		
		txtTarget = new Text(shell, SWT.BORDER);
		txtTarget.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtTarget.setText(FileUtils.getUserDirectoryPath());
		// txtTarget.setText("/xmpl/movies");
		
		Button btnSelectTarget = new Button(shell, SWT.NONE);
		GridData gd_btnSelectTarget = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnSelectTarget.widthHint = 100;
		btnSelectTarget.setLayoutData(gd_btnSelectTarget);
		btnSelectTarget.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectDirectory("Select Target Directory", txtTarget);
			}
		});
		btnSelectTarget.setText("Target Dir");
		
		final ProgressBar progressAnalyze = new ProgressBar(shell, SWT.NONE);
		progressAnalyze.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		progressAnalyze.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent event) {
				final String text = progressAnalyze.getSelection() + " of " + progressAnalyze.getMaximum() + " source files inspected.";
				final Point point = progressAnalyze.getSize();
				final FontMetrics fontMetrics = event.gc.getFontMetrics();
				final int stringWidth = fontMetrics.getAverageCharWidth() * text.length();
				final int stringHeight = fontMetrics.getHeight();
				final int xPos = (point.x - stringWidth) / 2;
				final int yPos = (point.y - stringHeight) / 2;
				event.gc.drawString(text, xPos, yPos, true);
			}
		});
		
		final Button btnAnalyze = new Button(shell, SWT.NONE);
		btnAnalyze.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doAnalyze(tree, progressAnalyze, btnAnalyze, null);
			}
		});
		GridData gd_btnAnalyze = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnAnalyze.widthHint = 100;
		btnAnalyze.setLayoutData(gd_btnAnalyze);
		btnAnalyze.setText("Analyze");
		
		final ProgressBar progressCopy = new ProgressBar(shell, SWT.NONE);
		progressCopy.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		progressCopy.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent event) {
				final String text = currentString + " of " + totalString;
				final Point point = progressCopy.getSize();
				final FontMetrics fontMetrics = event.gc.getFontMetrics();
				final int stringWidth = fontMetrics.getAverageCharWidth() * text.length();
				final int stringHeight = fontMetrics.getHeight();
				final int xPos = (point.x - stringWidth) / 2;
				final int yPos = (point.y - stringHeight) / 2;
				event.gc.drawString(text, xPos, yPos, true);
			}
		});

		Button btnCopy = new Button(shell, SWT.NONE);
		btnCopy.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnCopy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LOG.debug("copying: ");
				final List<File> sourceFiles = new LinkedList<File>();
				for (TreeItem treeItem : tree.getItems()) {
					if (treeItem.getChecked()) {
						LOG.debug("\t" + treeItem.getText());
						sourceFiles.add(new File(treeItem.getText()));
					}
				}
				LOG.debug("Total size: " + FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(sourceFiles)));
				disableAll();
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							FileUtils.copyToDirectory(sourceFiles, new File(txtTarget.getText()), new org.apache.commons.io.ProgressListener() {
								@Override
								public void onProgress(long current, long total) {
									currentString = FileUtils.byteCountToDisplaySize(current);
									totalString = FileUtils.byteCountToDisplaySize(total);
									progressCopy.setMaximum((int) (total/1000));
									progressCopy.setSelection((int) (current/1000));
								}
							});
							LOG.debug("Copy finished, cleaning up.");
							final File targetDir = new File(txtTarget.getText());
							new DirInfoCacher().removeCacheFile(targetDir);
							doAnalyze(tree, progressAnalyze, btnAnalyze, null);
							currentString = DEFAULT_CURRENT;
							totalString = DEFAULT_TOTAL;
							progressAnalyze.redraw();
							LOG.debug("Done.");
							enableAll();
						} catch (IOException e) {
							showError(e);
						}
					}
				});
			}
		});
		btnCopy.setText("Copy Stuff");
	}
	
	private void disableAll() {
		for (Control control : this.shell.getChildren()) {
			control.setEnabled(false);
		}
	}
	
	private void enableAll() {
		for (Control control : this.shell.getChildren()) {
			control.setEnabled(true);
		}
	}
	
	private String selectDirectory(final String title, final Text textWidget) {
		final DirectoryDialog selectSourceDialog = new DirectoryDialog(shell);
		selectSourceDialog.setFilterPath(textWidget.getText());
		selectSourceDialog.setText(title);
		final String sourceDir = selectSourceDialog.open();
		textWidget.setText(sourceDir);
		return sourceDir;
	}
	
	private void doAnalyze(final Tree tree, final ProgressBar progressAnalyze, final Button btnAnalyze, final Runnable doAfter) {
		tree.removeAll();
		disableAll();
		final TreeItem treeItem = new TreeItem(tree, SWT.NONE);
		treeItem.setText("Doing the analyze, plz wait...");
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					synchronized (tree) {
						final CopyRecommender copyRecommender = new CopyRecommender();
						final File sourceDir = new File(txtSource.getText());
						final File targetDir = new File(txtTarget.getText());
						final List<String> directorys = copyRecommender.getCopyRecommendations(sourceDir, targetDir, false, new ProgressListener() {
							@Override
							public void onProgress(final long current, final long total) {
								progressAnalyze.setMaximum((int) total);
								progressAnalyze.setSelection((int) current);
							}
						});
						tree.removeAll();
						for (final String directory : directorys) {
							final TreeItem treeItem = new TreeItem(tree, SWT.CHECK);
							treeItem.setText(directory);
						}
					}
					if (null != doAfter) {
						doAfter.run();
					}
				} catch (final Exception e) {
					showError(e);
				} finally {
					enableAll();
				}
			}
		});
	}

	private void showError(final Exception e) {
		LOG.error(e);
		tree.removeAll();
		final MessageBox messageBox = new MessageBox(shell);
		messageBox.setMessage(e.getMessage());
		messageBox.setText("Error");
		messageBox.open();
	}
	
}
