package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Survey.Survey.Form.Question;

import org.achartengine.chart.BarChart;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PieChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SurveyChartViewPagerAdapter extends PagerAdapter implements OnPageChangeListener {

	ArrayList<Integer> qVote;
	Question question;
	int nResponders;
	SurveyResultFragment controller;
	NotScrollableViewPager pager;
	int maxHeight;
	Point screenSize;
	LinearLayout pagerIndicator;
	Button switchChartTypeBT;
	
	final static private String showPieChart = "원 그래프 보기";
	final static private String showBarChart = "막대 그래프 보기";
	boolean scrolling = false; 
	boolean scrollable = true;
	
	private final static int ChartType_Pie = 0;
	private final static int ChartType_Bar = 1;

	private final static String PagerIndicatorTagPrefix = "indicator"; 
	
	private Resources getResources() {
		return controller.getResources();
	}
	
	private Activity getActivity() {
		return controller.getActivity();
	}
	
	@Override
	public int getCount() {
		// 현재 PagerAdapter에서 관리할 갯수를 반환 한다.
		return 2;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// instantiateItem메소드에서 생성한 객체를 이용할 것인지 여부를 반환 한다.
		return view.equals(object);
	}
	
	public SurveyChartViewPagerAdapter(SurveyResultFragment controller, NotScrollableViewPager pager, LinearLayout pagerIndicator, Button switchChartTypeBT, boolean scrollable, int nResponders, Question question, ArrayList<Integer> qVote) {
		this.controller = controller;
		this.scrollable = scrollable;
		this.pager = pager;
		this.pager.setScrollable(scrollable);
		this.pagerIndicator = pagerIndicator;
		this.pagerIndicator.setVisibility( scrollable ? View.VISIBLE : View.GONE);
		if(scrollable) 
			initPagerIndicator();
		
		this.question = question;
		this.nResponders = nResponders;
		this.qVote = qVote;
		maxHeight = 0;
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		this.screenSize = new Point();
		if(VERSION.SDK_INT < 13 ) {
			screenSize.x = display.getWidth();
			screenSize.y = display.getHeight();
		} else {
			display.getSize(screenSize);
		}
		
		
		this.switchChartTypeBT = switchChartTypeBT;
		this.switchChartTypeBT.setText(showBarChart);
		this.switchChartTypeBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				NotScrollableViewPager p = SurveyChartViewPagerAdapter.this.pager;
				int nextItem = (p.getCurrentItem() + 1) % p.getChildCount();
				p.setCurrentItem( nextItem , false);
				((Button)view).setText( nextItem % 2 == 0 ? showPieChart : showBarChart );
			}
		});
	}
	
//	public void renderChart() {
//		//drawBars(questionLL, qVote);
//	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// ViewPager에서 사용할 뷰객체 생성 및 등록 한다.
		LayoutInflater inflater = getActivity().getLayoutInflater();
		ViewGroup view = (ViewGroup)inflater.inflate(R.layout.survey_chart_unit, container, false);

		if(position == 0) {
			drawPie(view, qVote);
		} else if(position == 1){
			drawBars(view, qVote);
		}
		
		renderOptions(view, question, nResponders, qVote, position);
		container.addView(view);
		
		
		
		final float DIMEN_ChartHeightPX = screenSize.x;//getResources().getDimension(R.dimen.survey_chart_height);
		final float DIMEN_OptionHeightPX = getResources().getDimension(R.dimen.survey_option_result_content_height);		
		final float DIMEN_MarginPX = getResources().getDimension(R.dimen.gap);
		
		LayoutParams lp = pager.getLayoutParams();
		
		maxHeight = Math.max(maxHeight, (int)(DIMEN_ChartHeightPX + question.options().size() * DIMEN_OptionHeightPX + 0* DIMEN_MarginPX));
		if(lp == null) {
			lp = new LayoutParams(LayoutParams.MATCH_PARENT,maxHeight);
		} else {
			lp.height = maxHeight;
		}
		
		pager.setLayoutParams(lp);
		
		return view;
	}
	
	protected void drawPie(ViewGroup view, ArrayList<Integer> qVote) {
		// 그래프를 담당하는 부분
		DefaultRenderer renderer = new DefaultRenderer();
		String dummyLabel = "";
		
		
		// 그래프 각 portion에 대한 설정
		CategorySeries series = new CategorySeries(dummyLabel);
		for (int i = 0; i < qVote.size(); i++)
		{
			SimpleSeriesRenderer ssr = new SimpleSeriesRenderer();
			ssr.setColor(controller.COLORS[i % (controller.COLORS.length)]);
			renderer.addSeriesRenderer(ssr);
			
			series.add(dummyLabel, qVote.get(i));
		}

		
		// 차트 전체에 대한 설정
		renderer.setDisplayValues(false);
		renderer.setShowLabels(false);
		renderer.setShowLegend(false);
		renderer.setShowAxes(false);
		renderer.setShowCustomTextGrid(false);
		renderer.setShowGrid(false);
		renderer.setZoomEnabled(false);
		renderer.setExternalZoomEnabled(false);
		renderer.setClickEnabled(false);
		renderer.setPanEnabled(false);
		renderer.setMargins(new int[] { 0, 0, 0, 0 });
		renderer.setScale(1.25f);

		PieChart qChart = new PieChart(series, renderer);


		int diameter = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
		int viewWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
		int viewHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
		int marginW = (viewWidth - diameter) / 2;
		int marginH = (viewHeight - diameter) / 2;

		Bitmap bm = Bitmap.createBitmap(viewWidth, viewHeight, Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		Paint p = new Paint();
		qChart.draw(c, marginW, marginH, diameter + marginW, diameter + marginH, p);
		
		drawDonutHole(c, viewWidth, viewHeight, diameter, 0.5f);
		
		//LinearLayout qChartWrapper = (LinearLayout)view.findViewById(R.id.graphWrapper);
		ImageView qChartView = (ImageView) view.findViewById(R.id.graphView);
		qChartView.setImageBitmap(bm);
	}
	
	protected Integer getMax(ArrayList<Integer> integers) {
		Iterator<Integer> itr = integers.iterator();
		Integer result = Integer.MIN_VALUE;
		while(itr.hasNext()) {
			Integer temp = itr.next();
			if(result < temp)
				result = temp;
		}
		
		return result;
	}
	
	protected Integer getMin(ArrayList<Integer> integers) {
		Iterator<Integer> itr = integers.iterator();
		Integer result = Integer.MAX_VALUE;
		while(itr.hasNext()) {
			Integer temp = itr.next();
			if(result > temp)
				result = temp;
		}
		
		return result;
	}
	
	
	
	protected void drawBars(ViewGroup view, ArrayList<Integer> qVote) {
		
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		String dummyLabel = "";
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		CategorySeries series = new CategorySeries(dummyLabel);
		for (int i=0; i < qVote.size(); i++) {
			// 데이터 설정
			series.add(dummyLabel, qVote.get(i));
		}
		dataset.addSeries(series.toXYSeries());
		renderer.addSeriesRenderer(new SimpleSeriesRenderer());
		
		// 차트 전체에 대한 설정 
		renderer.setDisplayValues(true);
		renderer.setShowLabels(true);
		renderer.setShowLegend(false);
		renderer.setShowCustomTextGrid(false);
		renderer.setShowGrid(false);
		renderer.setExternalZoomEnabled(false);
		renderer.setClickEnabled(false);
		renderer.setZoomEnabled(false);
		renderer.setPanEnabled(false);
		//renderer.setMargins(new int[] { 0, 0, 0, 0 });
		//renderer.setScale(0.5f);

		// 축 설정
		renderer.setShowAxes(true);
		renderer.setShowGridY(true);
		renderer.setAxesColor(Color.BLACK);
		renderer.setLabelsColor(Color.BLACK);
		float labelTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
		renderer.setLabelsTextSize(labelTextSize);
		renderer.setXLabels(qVote.size());
		renderer.setYLabels(5);
		renderer.setYLabelsPadding(10000f);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setDisplayValues(true);
		
		Integer maxValue = getMax(qVote);
		Integer minValue = getMin(qVote);
		double gapValue = (maxValue - minValue) * 0.1;
		Integer maxBound = maxValue + (int) Math.round(gapValue);
		Integer minBound = minValue - (int) Math.round(gapValue);
		renderer.setYAxisMin( minBound );
		renderer.setYAxisMax( maxBound );
		
		//renderer.setMargins(new int[] {4,4,4,4});
		final float screenShirinkFactor = 0.80f;
		final float barWidthPX = (float) (screenSize.x * screenShirinkFactor / (qVote.size() * 1f));
		renderer.setBarWidth(barWidthPX);
		
		
		final float shiftFactor = 0.6f;
		final float leftShift = barWidthPX * shiftFactor;
		renderer.setBarSpacing( -3 * shiftFactor);
		
		
		final int[] margins = renderer.getMargins();
		
		BarChart qChart = new BarChart(dataset, renderer, Type.DEFAULT) {

			@Override
			public void drawSeries(Canvas canvas, Paint paint, List<Float> points,
					SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex, int startIndex) {
				
				int seriesNr = mDataset.getSeriesCount();
				
				paint.setStyle(Style.FILL);
				float halfDiffX = getHalfDiffX(points, points.size(), seriesNr);
				
				for (int i = 0 ; i< points.size(); i+=2) {
					paint.setColor( controller.COLORS[ (i/2) % controller.COLORS.length ]);
					
						float x = points.get(i);
						float y = points.get(i+1);
						
						drawBar(canvas, x+leftShift, yAxisValue, x, y, halfDiffX, seriesNr, seriesIndex, paint);
					
				}
			}
			
			@Override
			protected void drawXLabels(
					List<Double> xLabels, Double[] xTextLabelLocations,
					Canvas canvas, Paint paint, 
					int left, int top, int bottom, double xPixelsPerUnit, 
					double minX, double maxX) {
				
				boolean showLabels = mRenderer.isShowLabels();
				boolean showGrid = mRenderer.isShowGridX();
				float rHeight =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11, getResources().getDisplayMetrics());
				
				if(showLabels || showGrid) {
					
					for (int i=0; i< xLabels.size(); i++) {
						
						long number = Math.round(xLabels.get(i));
						float xPos = (float) (left + xPixelsPerUnit * ( number - minX )) + leftShift;
						String text = number + "번"; 
						if(showLabels) {
							paint.setColor(mRenderer.getLabelsColor());
							canvas.drawLine(xPos, bottom, xPos, bottom + rHeight, paint);
							drawText(canvas, text, xPos, bottom + 2*rHeight, paint, mRenderer.getXLabels());
						}
						if(showGrid) {
							paint.setColor(Color.DKGRAY);
							canvas.drawLine(xPos, bottom, xPos, top, paint);
						}
					}
					
				}
//				super.drawXLabels(xLabels, xTextLabelLocations, canvas, paint, left, top, bottom, xPixelsPerUnit, minX, maxX);
			}
//			
//			@Override
//			protected void drawXTextLabels(
//					Double[] xTextLabelLocations, 
//					Canvas canvas, Paint paint, boolean showLabels, 
//					int left, int top, int bottom, double xPixelsPerUnit, 
//					double minX, double maxX) {
//				
//				super.drawXTextLabels(xTextLabelLocations, canvas, paint, showLabels, left, top, bottom, xPixelsPerUnit, minX, maxX);
//			}
		};
		
		int viewWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, screenSize.x, getResources().getDisplayMetrics());
		int viewHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, screenSize.x, getResources().getDisplayMetrics());
		int chartWidth = (int)(viewWidth * screenShirinkFactor); 
		int chartHeight = (int)(viewHeight * 1.0f);
		
		int marginW = 0;//(viewWidth - chartWidth) / 2;
		int marginH = 0;//(viewHeight - chartHeight) / 2;
		
		Bitmap bm = Bitmap.createBitmap(viewWidth, viewHeight, Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		Paint p = new Paint();
		qChart.draw(c, marginW, marginH, chartWidth + marginW, chartHeight + marginH, p);
		
		//LinearLayout qChartWrapper = (LinearLayout)questionLL.findViewById(R.id.graphWrapper);
		ImageView qChartView = (ImageView) view.findViewById(R.id.graphView);
		qChartView.setImageBitmap(bm);
	}
	
	/*protected void drawBars(ViewGroup questionLL, ArrayList<Integer> qVote) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		String dummyLabel = "";
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		for (int i = 0; i < qVote.size(); i++)
		{
			// renderer에 각 데이터의 색을 지정해준다.
			SimpleSeriesRenderer ssr = new SimpleSeriesRenderer();
			ssr.setColor(COLORS[i % (COLORS.length)]);
			renderer.addSeriesRenderer(ssr);
			
			// 데이터 설정
			CategorySeries series = new CategorySeries(dummyLabel);
			series.add(dummyLabel, qVote.get(i));
			dataset.addSeries(series.toXYSeries());
		}
		
		
		// 차트 전체에 대한 설정
		renderer.setDisplayValues(true);
		renderer.setShowLabels(false);
		renderer.setShowLegend(false);
		renderer.setShowCustomTextGrid(false);
		renderer.setShowGrid(false);
		//renderer.setExternalZoomEnabled(false);
		//renderer.setClickEnabled(false);
		//renderer.setZoomEnabled(false);
		//renderer.setPanEnabled(false);
		renderer.setMargins(new int[] { 0, 0, 0, 0 });
		renderer.setScale(0.5f);

		// 축 설정
		renderer.setShowAxes(true);
		renderer.setXAxisMin(0);
		renderer.setYAxisMin(0);
		renderer.setAxesColor(Color.parseColor("#CCFFFFFF"));
		renderer.setLabelsColor(Color.parseColor("#EEFFFFFF"));
		//renderer.setXLabels(qVote.)
		renderer.setYLabels(5);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.CENTER);
		renderer.setBarSpacing(0.1f);
		
		BarChart qChart = new BarChart(dataset, renderer, Type.DEFAULT);

		int chartWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
		int chartHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
		int viewWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
		int viewHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
		int marginW = (viewWidth - chartWidth) / 2;
		int marginH = (viewHeight - chartHeight) / 2;
		
		Bitmap bm = Bitmap.createBitmap(viewWidth, viewHeight, Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		Paint p = new Paint();
		qChart.draw(c, marginW, marginH, chartWidth + marginW, chartHeight + marginH, p);
		
		ImageView qChartContainer = (ImageView) questionLL.findViewById(R.id.graphView);
		qChartContainer.setImageBitmap(bm);
	}*/
	
	protected void drawDonutHole(Canvas c, int viewW, int viewH, int original_diameter, float thickness) {
		// 도넛 효과를 위한 가운데 흰 원그래프에 대한 설정
		
		// 차트 전체에 대한 설정
		DefaultRenderer dummyRenderer =   new DefaultRenderer();
		dummyRenderer.setDisplayValues(false);
		dummyRenderer.setShowLabels(false);
		dummyRenderer.setShowLegend(false);
		dummyRenderer.setShowAxes(false);
		dummyRenderer.setShowCustomTextGrid(false);
		dummyRenderer.setShowGrid(false);
		dummyRenderer.setZoomEnabled(false);
		dummyRenderer.setExternalZoomEnabled(false);
		dummyRenderer.setClickEnabled(false);
		dummyRenderer.setPanEnabled(false);
		dummyRenderer.setMargins(new int[] { 0, 0, 0, 0 });
		dummyRenderer.setScale(1.25f);

		SimpleSeriesRenderer ssr = new SimpleSeriesRenderer();
		ssr.setColor(Color.parseColor("#FFFFFF"));
		dummyRenderer.addSeriesRenderer(ssr);
		
		CategorySeries dummySeries = new CategorySeries("DUMMY");
		dummySeries.add("DUMMY", 1);
		
		PieChart dummyChart = new PieChart(dummySeries, dummyRenderer);
		
		int diameter = (int)(original_diameter * thickness);
		int marginW = ( viewW - diameter )/2;
		int marginH = ( viewH - diameter )/2;
		
		dummyChart.draw(c, marginW, marginH, diameter , diameter, new Paint());
	}
	
	protected void renderOptions(ViewGroup parentView, Question question, int nResponders, ArrayList<Integer> qVote, int type) {
		// Option들에 대한 처리
		LinearLayout _optionsLL = (LinearLayout) parentView.findViewById(R.id.options);
		ArrayList<String> options = question.options();
		LayoutInflater inflater = getActivity().getLayoutInflater();
		// nResponders = options.size();
		for (int oi = 0; oi < options.size(); oi++) {
			String option = options.get(oi);
			LinearLayout optionLL = (LinearLayout) inflater.inflate(R.layout.survey_option_result, _optionsLL, false);
			
			TextView optionTitleTV = (TextView) optionLL.findViewById(R.id.title);
			optionTitleTV.setText(option);

			TextView optionContentTV = (TextView) optionLL.findViewById(R.id.content);
			int nThisOption = qVote.get(oi);
			String content = "";
			
			switch(type) {
			case ChartType_Pie :
				float percent = ((float) nThisOption / (float) nResponders * (float) 100.0);
				content = ((int) Math.round(percent)) + " %";
				break;
				
			case ChartType_Bar :
				content = nThisOption + " 명";
				break;
			}
			optionContentTV.setText(content);
			optionContentTV.setTextColor(controller.COLORS[oi % (controller.COLORS.length)]);
			
			_optionsLL.addView(optionLL);
		}
	}
	
	
//	@Override
//	public void destroyItem(ViewGroup container, int position, Object object) {
//		// View 객체를 삭제 한다.
//		container.removeView((View)object);
//	}
//	
//	@Override
//	public Parcelable saveState() {
//		// 현재 UI 상태를 저장하기 위해 Adapter와 Page 관련 인스턴스 상태를 저장 합니다. 
//		return super.saveState();
//	}
//	
//	@Override
//	public void restoreState(Parcelable state, ClassLoader loader) {
//		// saveState() 상태에서 저장했던 Adapter와 page를 복구 한다. 
//		super.restoreState(state, loader);
//	}
//	@Override
//	public void startUpdate(ViewGroup container) {
//		// 페이지 변경이 시작될때 호출 됩니다.
//		super.startUpdate(container);
//	}
//	
//	@Override
//	public void finishUpdate(ViewGroup container) {
//		// 페이지 변경이 완료되었을때 호출 됩니다.
//		super.finishUpdate(container);
//	}

	@Override
	public void onPageScrollStateChanged(int state) {
		return;
		//Log.e("VP", "onPageScrollStateChanged : " + state);
		// 기본/up : 0, down : 1, moved : 2
		
//		scrolling = (state % 2 == 1) ? true : false;
	}

	@Override
	public void onPageScrolled(int position, float ratio, int posPX) {
		return;
		//Log.e("VP", "onPageScrolled : " + position + ", " + ratio + ", " + posPX);
//		if(scrolling) {
//			if(0.1 < ratio && ratio < 0.3 ) {
//				pager.setCurrentItem( 1 , true);
//				scrolling = false;
//			} else if( 0.7 < ratio && ratio < 0.9 ) {
//				pager.setCurrentItem( 0 , true);
//				scrolling = false;
//			}
//			return;
//		}
	}

	@Override
	public void onPageSelected(int position) {
		//Log.e("VP", "onPageSelected" + position);
		if(this.scrollable)
			activePagerIndicator(position);
	}

	
	// pager indicator
	
	private void initPagerIndicator() {
		for(int i = 0; i < getCount(); i++) {

			ImageView bubble = new ImageView(getActivity());
			bubble.setTag(PagerIndicatorTagPrefix + i);
			LayoutParams lp = bubble.getLayoutParams();
			int diameter =  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getActivity().getResources().getDisplayMetrics());
			int padding = (int)(0.2 * diameter);
			if(lp == null) {
				lp = new LayoutParams(diameter, diameter);
			} else {
				lp.width = diameter;
				lp.height = diameter;
			}
			bubble.setLayoutParams(lp);
			bubble.setPadding(padding, padding, padding, padding);
			bubble.setImageResource(R.drawable.pager_indicator_bubble_inactive);
			
			pagerIndicator.addView(bubble);
		}
		
		activePagerIndicator(0);
	}
	
	private void activePagerIndicator (int position) {
		for(int i=0; i< pagerIndicator.getChildCount(); i++) {
			((ImageView)pagerIndicator.getChildAt(i)).setImageResource(R.drawable.pager_indicator_bubble_inactive);
		}
		ImageView selected = (ImageView)pagerIndicator.findViewWithTag(PagerIndicatorTagPrefix + position);
		selected.setImageResource(R.drawable.pager_indicator_bubble_active);
	}
}
