<template>
  <div class="dependencies-page">
    <q-card>
      <q-card-title>
        <h4>Dépendances </h4>
      </q-card-title>
      <q-card-separator></q-card-separator>
      <q-card-main>
        <div style="display: flex;flex-direction: column">
          <div>

            <p>Add <code>fixed</code> class to the containing div to make the nodes a fixed size. (<a
              href="javascript:toggleFixed();">Do it now</a>)</p>
          </div>
          <div class="ul-tree horizontal fixed">

            <ul>
              <li>
                <p>Root Page Root Page Root Page Root Page</p>
                <ul>
                  <li><p>Another</p></li>
                  <li><p>Something</p></li>
                  <li>
                    <p>Page</p>
                    <ul>
                      <li><p>Another</p></li>
                      <li><p>Something</p>
                        <ul>
                          <li><p>Another</p></li>
                          <li><p>Something</p></li>
                        </ul>
                      </li>
                    </ul>
                  </li>
                </ul>
              </li>
            </ul>
            </li>
            </ul>
            <p>&nbsp;</p>
          </div>

          <div class="ul-tree vertical invisible">
            <ul>
              <li>
                <p>Root Page</p>
                <ul>
                  <li><p>Another</p></li>
                  <li><p>Something</p></li>
                  <li><p>Something</p></li>
                  <li>
                    <p>Page</p>
                    <ul>
                      <li><p>Another</p></li>
                      <li><p>Something</p></li>
                    </ul>
                  </li>
                </ul>
              </li>
            </ul>
          </div>
        </div>
      </q-card-main>
    </q-card>
  </div>
</template>
<script>
  import {
    QCard,
    QCardTitle,
    QCardSeparator,
    QCardMain,
    QInput,
    QBtn,
    QSelect,
    QTabs,
    QRouteTab,
    QList,
    QItem,
    QItemMain,
    QRadio,
    QInfiniteScroll,
    QSpinnerDots
  } from 'quasar';

  export default {
    name: 'Configuration',
    components: {
      QCard,
      QCardTitle,
      QCardSeparator,
      QCardMain,
      QInput,
      QBtn,
      QSelect,
      QTabs,
      QRouteTab,
      QList,
      QItem,
      QItemMain,
      QRadio,
      QInfiniteScroll,
      QSpinnerDots
    }
  }
</script>
<style scoped>
  .dependencies-page .invisible {
    display: none;
  }

  .ul-tree.horizontal ul {
    position: relative;
    margin: 0;
    padding: 0 0 0 1em;
    list-style: none;
  }

  .ul-tree.horizontal ul:before {
    position: absolute;
    content: '';
    top: 50%;
    left: 0;
    width: 1em;
    border-top: 1px solid #999;
  }

  .ul-tree.horizontal li {
    display: inline-block;
    position: relative;
    display: flex;
    flex-direction: row;
    align-items: center;
    padding: 0 0 0 1em;
  }

  .ul-tree.horizontal li:before {
    position: absolute;
    content: '';
    top: 0;
    left: 0;
    bottom: 0;
    border-left: 1px solid #999;
  }

  .ul-tree.horizontal li:first-child:before {
    top: 50%;
  }

  .ul-tree.horizontal li:last-child:before {
    bottom: 50%;
  }

  .ul-tree.horizontal li p {
    display: inline-block;
    position: relative;
    background-color: #eee;
    border: 1px solid #999;
    margin: 1em 0;
    padding: 0.5em;
    text-align: center;
  }

  .ul-tree.horizontal li p:before {
    position: absolute;
    content: '';
    top: 50%;
    left: -1em;
    width: 1em;
    border-top: 1px solid #999;
  }

  .ul-tree.horizontal > ul {
    padding: 0;
  }

  .ul-tree.horizontal > ul:before {
    display: none;
  }

  .ul-tree.horizontal > ul > li:before {
    display: none;
  }

  .ul-tree.horizontal > ul > li > p:before {
    display: none;
  }

  .ul-tree.horizontal.fixed p {
    width: 7em;
  }

  .ul-tree.vertical ul {
    position: relative;
    margin: 0;
    padding: 1em 0 0 0;
    list-style: none;
    display: flex;
    flex-direction: row;
  }

  .ul-tree.vertical ul:before {
    position: absolute;
    content: '';
    left: 50%;
    top: 0;
    height: 1em;
    border-left: 1px solid #999;
  }

  .ul-tree.vertical li {
    display: inline-block;
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 1em 0 0 0;
  }

  .ul-tree.vertical li:before {
    position: absolute;
    content: '';
    top: 0;
    left: 0;
    right: 0;
    border-top: 1px solid #999;
  }

  .ul-tree.vertical li:first-child:before {
    left: 50%;
  }

  .ul-tree.vertical li:last-child:before {
    right: 50%;
  }

  .ul-tree.vertical li p {
    display: inline-block;
    position: relative;
    background-color: #eee;
    border: 1px solid #999;
    margin: 0 1em;
    padding: 0.5em;
    text-align: center;
  }

  .ul-tree.vertical li p:before {
    position: absolute;
    content: '';
    left: 50%;
    top: -1em;
    height: 1em;
    border-left: 1px solid #999;
  }

  .ul-tree.vertical > ul {
    padding: 0;
  }

  .ul-tree.vertical > ul:before {
    display: none;
  }

  .ul-tree.vertical > ul > li:before {
    display: none;
  }

  .ul-tree.vertical > ul > li > p:before {
    display: none;
  }

  .ul-tree.vertical.fixed p {
    width: 4em;
    height: 5em;
  }

</style>
