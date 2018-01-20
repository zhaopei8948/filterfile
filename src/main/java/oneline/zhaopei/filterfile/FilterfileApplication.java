package oneline.zhaopei.filterfile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class FilterfileApplication extends JFrame {

	private static final Log logger = LogFactory.getLog(FilterfileApplication.class);

	private JTextField tfFilterFile = null;

	private JTextField tMessageDir = null;

	private JRadioButton rbEqu = null;
	private JRadioButton rbNoEqu = null;

	public FilterfileApplication() {
		initUI();
	}

	private void initUI() {
		Container pane = getContentPane();
		Box hbox1 = Box.createHorizontalBox();
		JLabel lFilterFile = new JLabel("选择过滤文本:");
		tfFilterFile = new JTextField(30);
		tfFilterFile.setEditable(false);
		tfFilterFile.setMaximumSize(tfFilterFile.getPreferredSize());
		tfFilterFile.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectFile(tfFilterFile, false);
			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});
		JButton bFilterFileSelect = new JButton("选择文件");
		bFilterFileSelect.addActionListener((ActionEvent event) -> {
		    selectFile(tfFilterFile, false);
		});
		hbox1.add(lFilterFile);
		hbox1.add(tfFilterFile);
		hbox1.add(bFilterFileSelect);

		Box hbox2 = Box.createHorizontalBox();
		JLabel lMessageDir = new JLabel("选择报文所在目录:");
		tMessageDir = new JTextField(30);
		tMessageDir.setEditable(false);
		tMessageDir.setMaximumSize(tMessageDir.getPreferredSize());
		tMessageDir.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectFile(tMessageDir, true);
			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});
		JButton bMessageSelect = new JButton("选择目录");
		bMessageSelect.addActionListener((ActionEvent event) -> {
			selectFile(tMessageDir, true);
		});

		hbox2.add(lMessageDir);
		hbox2.add(tMessageDir);
		hbox2.add(bMessageSelect);

		Box hbox3 = Box.createHorizontalBox();
		rbEqu = new JRadioButton("过滤相等");
		rbNoEqu = new JRadioButton("过滤不相等", true);
		ButtonGroup bg = new ButtonGroup();
		bg.add(rbEqu);
		bg.add(rbNoEqu);
		hbox3.add(rbEqu);
		hbox3.add(rbNoEqu);

		Box hbox4 = Box.createHorizontalBox();
		JButton bExit = new JButton("退出");
		bExit.addActionListener((ActionEvent event) -> {
			System.exit(0);
		});
		JButton bFilter = new JButton("过滤文件");
		bFilter.addActionListener((ActionEvent event) -> {
			filter();
		});
		hbox4.add(bExit);
		hbox4.add(bFilter);

		Box vbox = Box.createVerticalBox();
		vbox.add(hbox1);
		vbox.add(hbox2);
		vbox.add(hbox3);
		vbox.add(Box.createVerticalGlue());
		vbox.add(hbox4);
		pane.add(vbox, BorderLayout.CENTER);

		setTitle("过滤报文工具");
		setSize(600, 150);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	private File selectFile(JTextField textField, boolean onlySelectDir) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(onlySelectDir ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
		fileChooser.showDialog(new JLabel(), "选择");
		File file = fileChooser.getSelectedFile();
		if (null != file) {
			textField.setText(file.getAbsolutePath());
		}
		return file;
	}

	private void filter() {
		String sFilterFile = this.tfFilterFile.getText();
		BufferedReader br = null;
		StringWriter sw = new StringWriter();
		String sMessageDir = this.tMessageDir.getText();
		File messageDir = null;
		String billno = null;
		String name = null;
		boolean isEqu = this.rbEqu.isSelected();
		Map<String, Integer> sm = new HashMap<String, Integer>();
		if (StringUtils.isEmpty(sFilterFile)) {
			JOptionPane.showMessageDialog(null, "请选择过滤文件", "错误提示", JOptionPane.ERROR_MESSAGE);
			return;
		} else if (StringUtils.isEmpty(sMessageDir)) {
			JOptionPane.showMessageDialog(null, "请选择报文所在目录", "错误提示", JOptionPane.ERROR_MESSAGE);
			return;
		} else {
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(sFilterFile), "GBK"));
				//while (!StringUtils.isEmpty((billno = br.readLine().trim()))) {
				while (null != (billno = br.readLine())) {
					sm.put(billno, 1);
				}

				messageDir = new File(sMessageDir);
				if (!messageDir.exists()) {
					JOptionPane.showMessageDialog(null, "报文目录不存在", "错误提示", JOptionPane.ERROR_MESSAGE);
					return;
				}
				for (File f: messageDir.listFiles()) {
					name = f.getName().substring(0, f.getName().indexOf("_"));
					if ((isEqu && sm.containsKey(name))
							|| (!isEqu && !sm.containsKey(name))) {
						logger.debug("equ is [" + isEqu + "] name: [" + name + "] delete file [" + f.getName() + "] is [" + f.delete() + "]");
					}
				}
			} catch (Exception e) {
				e.printStackTrace(new PrintWriter(sw));
				JOptionPane.showMessageDialog(null, sw.toString(), "错误提示", JOptionPane.ERROR_MESSAGE);
				return;
			}

		}
		JOptionPane.showMessageDialog(null, "过滤完成", "成功", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void main(String[] args) {
		//SpringApplication.run(FilterfileApplication.class, args);
		ConfigurableApplicationContext ctx = new SpringApplicationBuilder(FilterfileApplication.class)
				.headless(false).run(args);

		EventQueue.invokeLater(() -> {
			FilterfileApplication ex = ctx.getBean(FilterfileApplication.class);
			ex.setVisible(true);
		});
	}
}
