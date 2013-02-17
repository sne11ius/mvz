package nu.wasis.mvz.gui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import nu.wasis.mvz.CopyRecommender;
import nu.wasis.mvz.ProgressListener;

import org.apache.commons.io.FileUtils;
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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class MainWindow {

	private static final Logger LOG = Logger.getLogger(MainWindow.class);
	
	protected Shell shell;
	private Text txtSource;
	private Text txtTarget;

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
		
		final Tree tree = new Tree(shell, SWT.BORDER | SWT.CHECK);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		txtSource = new Text(shell, SWT.BORDER);
		txtSource.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtSource.setText(FileUtils.getUserDirectoryPath());
		
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
				tree.removeAll();
				btnAnalyze.setEnabled(false);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						try {
							synchronized (tree) {
								final CopyRecommender copyRecommender = new CopyRecommender();
								final File sourceDir = new File(txtSource.getText());
								final File targetDir = new File(txtTarget.getText());
								List<String> directorys = copyRecommender.getCopyRecommendations(sourceDir, targetDir, false, new ProgressListener() {
									@Override
									public void onProgess(long current, long total) {
										progressAnalyze.setMaximum((int) total);
										progressAnalyze.setSelection((int) current);
									}
								});
								for (String directory : directorys) {
									TreeItem treeItem = new TreeItem(tree, SWT.CHECK);
									treeItem.setText(directory);
								}
							}
						} catch (IOException e1) {
							final MessageBox messageBox = new MessageBox(shell);
							messageBox.setMessage("Error");
							messageBox.setText(e1.getLocalizedMessage());
							messageBox.open();
						} finally {
							btnAnalyze.setEnabled(true);
						}
					}
				});
			}
		});
		GridData gd_btnAnalyze = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnAnalyze.widthHint = 100;
		btnAnalyze.setLayoutData(gd_btnAnalyze);
		btnAnalyze.setText("Analyze");
		
		ProgressBar progressCopy = new ProgressBar(shell, SWT.NONE);
		progressCopy.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Button btnCopy = new Button(shell, SWT.NONE);
		btnCopy.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnCopy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.YES | SWT.NO | SWT.CANCEL);
				messageBox.setText("Not implemented");
				messageBox.setMessage("Totally not implemented yet, try anyway?");
				int messageBoxResult = messageBox.open();
				if (SWT.YES == messageBoxResult) {
					LOG.info("(isIdiot? user)");
					LOG.info("\ttrue");
				}
			}
		});
		btnCopy.setText("Copy Stuff");
	}
	
	private String selectDirectory(final String title, final Text textWidget) {
		final DirectoryDialog selectSourceDialog = new DirectoryDialog(shell);
		selectSourceDialog.setFilterPath(textWidget.getText());
		selectSourceDialog.setText(title);
		final String sourceDir = selectSourceDialog.open();
		textWidget.setText(sourceDir);
		return sourceDir;
	}

}
