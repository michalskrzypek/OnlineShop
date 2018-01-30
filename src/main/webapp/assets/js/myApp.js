var $table = $('#products_table');

if ($table.length) {

	var jsonUrl = '';

	if (window.categoryId == '') {
		jsonUrl = window.contextRoot + '/json/data/all/products';
	} else {
		jsonUrl = window.contextRoot + '/json/data/category/'
				+ window.categoryId + '/products';
	}

	$table
			.DataTable({
				lengthMenu : [ [ 3, 5, 10, -1 ],
						[ '3 Products', '5 Products', '10 Products', 'All' ] ],
				pageLength : 5,
				ajax : {
					url : jsonUrl,
					dataSrc : ''
				},
				columns : [
						{
							data : 'code',
							mRender : function(data, type, row) {
								var src = '<img src="' + window.contextRoot
										+ '/resources/images/' + data
										+ '.jpg" class="dataTableImg"/>';
								return src;
							}

						},

						{
							data : 'name'
						},
						{
							data : 'brand'
						},
						{
							data : 'unitPrice',
							mRender : function(data, type, row) {
								return '&#36;' + data
							}
						},
						{
							data : 'id',
							mRender : function(data, type, row) {

								var str = '<a href="'
										+ window.contextRoot
										+ '/show/product/'
										+ data
										+ '" class="btn btn-secondary"><span class="glyphicon glyphicon-eye-open"></span></a>';
								return str;
							}
						},
						{
							data : 'id',
							mRender : function(data, type, row) {
								if (row.quantity == 0) {
									var str = '<a href="';
									str += '" class="btn btn-success disabled"><span class="glyphicon glyphicon-shopping-cart"></span></a>';
									return str;
								} else {
									var str = '<a href="';
									str += window.contextRoot
											+ '/cart/add/product/'
											+ data
											+ '" class="btn btn-success"><span class="glyphicon glyphicon-shopping-cart"></span></a>';
									return str;
								}

							}

						}

				]

			});

}

var $adminTable = $('#admin_products_table');

jsonURL = window.contextRoot + '/json/data/admin/all/products';

$adminTable
		.DataTable({
			lengthMenu : [
					[ 10, 20, 50, -1 ],
					[ '10 Products', '20 Products', '50 Products',
							'All products' ] ],
			pageLength : 20,
			ajax : {
				url : jsonURL,
				dataSrc : ''
			},

			columns : [

					{
						data : 'id'
					},
					{
						data : 'code',
						mRender : function(data, type, row) {
							var src = '<img src="' + window.contextRoot
									+ '/resources/images/' + data
									+ '.jpg" class="dataTableImg"/>';
							return src;
						}
					},

					{
						data : 'name'
					},
					{
						data : 'brand'
					},
					{

						data : 'unitPrice',

					},
					{
						data : 'quantity'
					},
					{
						data : 'active',
						/* data2:'id', */
						mRender : function(data, type, row) {
							var htmlOption = '';
							if (data) {
								htmlOption = ' <form action="'+window.contextRoot+'/manage/product/activity/'
										+ row.id
										+ '" method="post"><input type="radio" name="activity" value="true" checked> Active<br><input type="radio" name="activity" value="false"> Inactive<br>';

							} else {
								htmlOption = ' <form action="'+window.contextRoot+'/manage/product/activity/'
										+ row.id
										+ '" method="post"><input type="radio" name="activity" value="true"> Active<br><input type="radio" name="activity" value="false" checked> Inactive<br>';

							}

							htmlOption += '<input type="submit" value="OK"/></form>';
							return htmlOption;
						}
					},
					{
						data : 'id',
						mRender : function(data, type, row) {

							var str = '<a href="'
									+ window.contextRoot
									+ '/manage/product/'
									+ data
									+ '" class="btn btn-warning"><span class="glyphicon glyphicon-pencil"></span></a>';
							return str;
						}
					},
					{
						data : 'id',
						mRender : function(data, type, row) {

							var str = '<a href="'
									+ window.contextRoot
									+ '/manage/product/delete/'
									+ data
									+ '" class="btn btn-danger"><span class="glyphicon glyphicon-trash"></span></a>';
							
							return str;
						}
					},

			]

		});